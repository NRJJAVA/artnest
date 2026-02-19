package com.artnest.service.serviceImpl;

import com.artnest.dto.BookingResponse;
import com.artnest.dto.CreateBookingRequest;
import com.artnest.entity.Booking;
import com.artnest.entity.Users;
import com.artnest.enums.BookingStatus;
import com.artnest.enums.UserRole;
import com.artnest.exception.ConflictException;
import com.artnest.exception.ResourceNotFoundException;
import com.artnest.repository.BookingRepository;
import com.artnest.repository.UsersRepository;
import com.artnest.service.BookingService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UsersRepository usersRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, UsersRepository usersRepository) {
        this.bookingRepository = bookingRepository;
        this.usersRepository = usersRepository;
    }

    @Override
    @Transactional
    public BookingResponse createBooking(String requesterEmail, CreateBookingRequest request) {
        Users customer = findUserByEmail(requesterEmail);
        Users artist = usersRepository.findById(request.getArtistId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with id: " + request.getArtistId()));

        if (customer.getId().equals(artist.getId())) {
            throw new ConflictException("You cannot create a booking with yourself");
        }

        boolean isArtist = artist.getRoles().stream()
                .anyMatch(role -> role.getRole() == UserRole.ARTIST);
        if (!isArtist) {
            throw new ConflictException("Selected user is not an artist");
        }

        int durationMinutes = request.getDurationMinutes() == null ? 60 : request.getDurationMinutes();
        validateDuration(durationMinutes);
        validateSlotBoundary(request.getBookingDate());
        LocalDateTime bookingStart = request.getBookingDate();
        LocalDateTime bookingEnd = bookingStart.plusMinutes(durationMinutes);

        List<Booking> overlaps = bookingRepository.findByArtist_IdOrderByBookingDateDesc(artist.getId()).stream()
                .filter(booking -> booking.getStatus() == BookingStatus.PENDING || booking.getStatus() == BookingStatus.ACCEPTED)
                .filter(booking -> {
                    LocalDateTime existingStart = booking.getBookingDate();
                    int existingDuration = booking.getDurationMinutes() == null || booking.getDurationMinutes() <= 0
                            ? 60 : booking.getDurationMinutes();
                    LocalDateTime existingEnd = booking.getBookingEndDate() == null
                            ? existingStart.plusMinutes(existingDuration)
                            : booking.getBookingEndDate();
                    return existingStart.isBefore(bookingEnd) && existingEnd.isAfter(bookingStart);
                })
                .toList();
        if (!overlaps.isEmpty()) {
            throw new ConflictException("Selected slot is not available for this artist");
        }

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setArtist(artist);
        booking.setBookingDate(bookingStart);
        booking.setBookingEndDate(bookingEnd);
        booking.setDurationMinutes(durationMinutes);
        booking.setArtType(request.getArtType().trim());
        booking.setLocation(request.getLocation().trim());
        booking.setDescription(request.getDescription().trim());
        booking.setStatus(BookingStatus.PENDING);

        Booking saved = bookingRepository.save(booking);
        return toResponse(saved);
    }

    @Override
    public List<BookingResponse> getCustomerBookings(String requesterEmail) {
        Users customer = findUserByEmail(requesterEmail);
        return bookingRepository.findByCustomer_IdOrderByBookingDateDesc(customer.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<BookingResponse> getArtistBookings(String requesterEmail) {
        Users artist = findUserByEmail(requesterEmail);
        boolean hasArtistRole = artist.getRoles().stream().anyMatch(role -> role.getRole() == UserRole.ARTIST);
        if (!hasArtistRole) {
            throw new ConflictException("Artist role is required to access artist bookings");
        }

        return bookingRepository.findByArtist_IdOrderByBookingDateDesc(artist.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public BookingResponse updateBookingStatus(String requesterEmail, Long bookingId, BookingStatus status) {
        Users artist = findUserByEmail(requesterEmail);
        boolean hasArtistRole = artist.getRoles().stream().anyMatch(role -> role.getRole() == UserRole.ARTIST);
        if (!hasArtistRole) {
            throw new ConflictException("Artist role is required to update booking status");
        }

        if (status != BookingStatus.ACCEPTED && status != BookingStatus.REJECTED) {
            throw new ConflictException("Artist can only update status to ACCEPTED or REJECTED");
        }

        Booking booking = bookingRepository.findByIdAndArtist_Id(bookingId, artist.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found for this artist: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException("Only pending bookings can be updated");
        }

        booking.setStatus(status);
        Booking updated = bookingRepository.save(booking);
        return toResponse(updated);
    }

    @Override
    @Transactional
    public BookingResponse extendBooking(String requesterEmail, Long bookingId, Integer additionalMinutes) {
        if (additionalMinutes == null || additionalMinutes <= 0 || additionalMinutes % 30 != 0) {
            throw new ConflictException("additionalMinutes must be a positive multiple of 30");
        }

        Users customer = findUserByEmail(requesterEmail);
        Booking booking = bookingRepository.findByIdAndCustomer_Id(bookingId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found for this customer: " + bookingId));

        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.ACCEPTED) {
            throw new ConflictException("Only pending or accepted bookings can be extended");
        }

        int currentDuration = booking.getDurationMinutes() == null ? 60 : booking.getDurationMinutes();
        int newDuration = currentDuration + additionalMinutes;
        validateDuration(newDuration);

        LocalDateTime start = booking.getBookingDate();
        LocalDateTime newEnd = start.plusMinutes(newDuration);

        List<Booking> overlaps = bookingRepository.findByArtist_IdOrderByBookingDateDesc(booking.getArtist().getId()).stream()
                .filter(b -> !b.getId().equals(booking.getId()))
                .filter(b -> b.getStatus() == BookingStatus.PENDING || b.getStatus() == BookingStatus.ACCEPTED)
                .filter(b -> {
                    LocalDateTime existingStart = b.getBookingDate();
                    int existingDuration = b.getDurationMinutes() == null || b.getDurationMinutes() <= 0
                            ? 60 : b.getDurationMinutes();
                    LocalDateTime existingEnd = b.getBookingEndDate() == null
                            ? existingStart.plusMinutes(existingDuration)
                            : b.getBookingEndDate();
                    return existingStart.isBefore(newEnd) && existingEnd.isAfter(start);
                })
                .toList();

        if (!overlaps.isEmpty()) {
            throw new ConflictException("Booking cannot be extended due to slot conflict");
        }

        booking.setDurationMinutes(newDuration);
        booking.setBookingEndDate(newEnd);
        Booking updated = bookingRepository.save(booking);
        return toResponse(updated);
    }

    private Users findUserByEmail(String email) {
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
    }

    private void validateDuration(int durationMinutes) {
        if (durationMinutes < 60 || durationMinutes > 240 || durationMinutes % 30 != 0) {
            throw new ConflictException("durationMinutes must be 60 to 240 in 30-minute steps");
        }
    }

    private void validateSlotBoundary(LocalDateTime start) {
        int minute = start.getMinute();
        if (minute != 0 || start.getSecond() != 0 || start.getNano() != 0) {
            throw new ConflictException("bookingDate must be aligned to 1-hour slot starts");
        }
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getCustomer().getId(),
                booking.getCustomer().getFullName(),
                booking.getCustomer().getAddress(),
                booking.getCustomer().getGender(),
                booking.getArtist().getId(),
                booking.getArtist().getFullName(),
                booking.getBookingDate(),
                booking.getBookingEndDate(),
                booking.getDurationMinutes(),
                booking.getArtType(),
                booking.getLocation(),
                booking.getDescription(),
                booking.getStatus(),
                booking.getCreatedAt()
        );
    }
}
