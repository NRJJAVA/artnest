package com.artnest.service.serviceImpl;

import com.artnest.dto.ArtistAvailabilityResponse;
import com.artnest.dto.ArtistTimeOffResponse;
import com.artnest.dto.AvailableSlotResponse;
import com.artnest.dto.CreateArtistTimeOffRequest;
import com.artnest.dto.UpsertArtistAvailabilityRequest;
import com.artnest.entity.ArtistAvailability;
import com.artnest.entity.ArtistTimeOff;
import com.artnest.entity.Booking;
import com.artnest.entity.Users;
import com.artnest.enums.BookingStatus;
import com.artnest.enums.UserRole;
import com.artnest.exception.ConflictException;
import com.artnest.exception.ResourceNotFoundException;
import com.artnest.repository.ArtistAvailabilityRepository;
import com.artnest.repository.ArtistTimeOffRepository;
import com.artnest.repository.BookingRepository;
import com.artnest.repository.UsersRepository;
import com.artnest.service.ArtistScheduleService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ArtistScheduleServiceImpl implements ArtistScheduleService {
    private static final DateTimeFormatter SLOT_TIME_FORMAT = DateTimeFormatter.ofPattern("h:mm a");

    private final UsersRepository usersRepository;
    private final ArtistAvailabilityRepository artistAvailabilityRepository;
    private final ArtistTimeOffRepository artistTimeOffRepository;
    private final BookingRepository bookingRepository;

    public ArtistScheduleServiceImpl(UsersRepository usersRepository,
                                     ArtistAvailabilityRepository artistAvailabilityRepository,
                                     ArtistTimeOffRepository artistTimeOffRepository,
                                     BookingRepository bookingRepository) {
        this.usersRepository = usersRepository;
        this.artistAvailabilityRepository = artistAvailabilityRepository;
        this.artistTimeOffRepository = artistTimeOffRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public ArtistAvailabilityResponse upsertAvailability(String requesterEmail, UpsertArtistAvailabilityRequest request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new ConflictException("endTime must be greater than startTime");
        }

        Users artist = findArtistByEmail(requesterEmail);

        List<ArtistAvailability> all = artistAvailabilityRepository
                .findByArtist_IdOrderByDayOfWeekAscStartTimeAsc(artist.getId());

        ArtistAvailability availability = all.stream()
                .filter(item -> item.getDayOfWeek() == request.getDayOfWeek()
                        && item.getStartTime().equals(request.getStartTime())
                        && item.getEndTime().equals(request.getEndTime()))
                .findFirst()
                .orElseGet(() -> {
                    ArtistAvailability created = new ArtistAvailability();
                    created.setArtist(artist);
                    return created;
                });

        availability.setDayOfWeek(request.getDayOfWeek());
        availability.setStartTime(request.getStartTime());
        availability.setEndTime(request.getEndTime());
        availability.setSlotDurationMinutes(request.getSlotDurationMinutes());
        if (request.getActive() != null) {
            availability.setIsActive(request.getActive());
        }

        ArtistAvailability saved = artistAvailabilityRepository.save(availability);
        return toAvailabilityResponse(saved);
    }

    @Override
    public List<ArtistAvailabilityResponse> getMyAvailabilities(String requesterEmail) {
        Users artist = findArtistByEmail(requesterEmail);
        return artistAvailabilityRepository.findByArtist_IdOrderByDayOfWeekAscStartTimeAsc(artist.getId())
                .stream()
                .map(this::toAvailabilityResponse)
                .toList();
    }

    @Override
    @Transactional
    public ArtistTimeOffResponse createTimeOff(String requesterEmail, CreateArtistTimeOffRequest request) {
        if (!request.getEndDateTime().isAfter(request.getStartDateTime())) {
            throw new ConflictException("endDateTime must be greater than startDateTime");
        }

        Users artist = findArtistByEmail(requesterEmail);

        ArtistTimeOff timeOff = new ArtistTimeOff();
        timeOff.setArtist(artist);
        timeOff.setStartDateTime(request.getStartDateTime());
        timeOff.setEndDateTime(request.getEndDateTime());
        timeOff.setReason(request.getReason() == null ? null : request.getReason().trim());

        ArtistTimeOff saved = artistTimeOffRepository.save(timeOff);
        return toTimeOffResponse(saved);
    }

    @Override
    public List<ArtistTimeOffResponse> getMyTimeOff(String requesterEmail) {
        Users artist = findArtistByEmail(requesterEmail);
        return artistTimeOffRepository.findByArtist_IdOrderByStartDateTimeDesc(artist.getId())
                .stream()
                .map(this::toTimeOffResponse)
                .toList();
    }

    @Override
    public List<AvailableSlotResponse> getAvailableSlots(Long artistId, LocalDate date) {
        Users artist = usersRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with id: " + artistId));
        boolean hasArtistRole = artist.getRoles().stream().anyMatch(role -> role.getRole() == UserRole.ARTIST);
        if (!hasArtistRole) {
            throw new ConflictException("Selected user is not an artist");
        }

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<ArtistAvailability> dayAvailability = artistAvailabilityRepository
                .findByArtist_IdAndIsActiveTrueAndDayOfWeekOrderByStartTimeAsc(artistId, dayOfWeek);
        if (dayAvailability.isEmpty()) {
            return List.of();
        }

        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

        List<Booking> blockingBookings = bookingRepository.findByArtist_IdOrderByBookingDateDesc(artistId).stream()
                .filter(booking -> booking.getStatus() == BookingStatus.PENDING || booking.getStatus() == BookingStatus.ACCEPTED)
                .filter(booking -> {
                    LocalDateTime bookingStart = booking.getBookingDate();
                    int duration = booking.getDurationMinutes() == null || booking.getDurationMinutes() <= 0
                            ? 60 : booking.getDurationMinutes();
                    LocalDateTime bookingEnd = booking.getBookingEndDate() == null
                            ? bookingStart.plusMinutes(duration)
                            : booking.getBookingEndDate();
                    return bookingStart.isBefore(dayEnd) && bookingEnd.isAfter(dayStart);
                })
                .toList();
        List<ArtistTimeOff> timeOffBlocks = artistTimeOffRepository
                .findByArtist_IdAndEndDateTimeAfterAndStartDateTimeBefore(artistId, dayStart, dayEnd);

        List<AvailableSlotResponse> result = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (ArtistAvailability availability : dayAvailability) {
            LocalDateTime cursor = date.atTime(availability.getStartTime());
            LocalDateTime windowEnd = date.atTime(availability.getEndTime());
            int step = 60;

            while (!cursor.plusMinutes(60).isAfter(windowEnd)) {
                LocalDateTime slotStart = cursor;
                LocalDateTime baseEnd = cursor.plusMinutes(60);

                if (slotStart.isAfter(now)
                        && !overlapsBookings(slotStart, baseEnd, blockingBookings)
                        && !overlapsTimeOff(slotStart, baseEnd, timeOffBlocks)) {
                    List<Integer> durations = buildPossibleDurations(
                            slotStart,
                            windowEnd,
                            blockingBookings,
                            timeOffBlocks
                    );
                    if (!durations.isEmpty()) {
                        int maxDuration = durations.get(durations.size() - 1);
                        LocalDateTime maxEnd = slotStart.plusMinutes(maxDuration);
                        List<Integer> allowedExtensions = durations.stream()
                                .filter(duration -> duration > 60)
                                .map(duration -> duration - 60)
                                .toList();
                        result.add(new AvailableSlotResponse(
                                slotStart,
                                baseEnd,
                                toDisplayRange(slotStart, baseEnd),
                                maxEnd.format(SLOT_TIME_FORMAT),
                                allowedExtensions
                        ));
                    }
                }

                cursor = cursor.plusMinutes(step);
            }
        }

        return result.stream()
                .sorted(Comparator.comparing(AvailableSlotResponse::getSlotStartDateTime))
                .toList();
    }

    private List<Integer> buildPossibleDurations(
            LocalDateTime slotStart,
            LocalDateTime windowEnd,
            List<Booking> blockingBookings,
            List<ArtistTimeOff> timeOffBlocks
    ) {
        List<Integer> possible = new ArrayList<>();
        for (int duration = 60; duration <= 240; duration += 30) {
            LocalDateTime slotEnd = slotStart.plusMinutes(duration);
            if (slotEnd.isAfter(windowEnd)) {
                break;
            }
            if (!overlapsBookings(slotStart, slotEnd, blockingBookings)
                    && !overlapsTimeOff(slotStart, slotEnd, timeOffBlocks)) {
                possible.add(duration);
            } else {
                break;
            }
        }
        return possible;
    }

    private String toDisplayRange(LocalDateTime start, LocalDateTime end) {
        return start.format(SLOT_TIME_FORMAT) + " - " + end.format(SLOT_TIME_FORMAT);
    }

    private boolean overlapsBookings(LocalDateTime start, LocalDateTime end, List<Booking> bookings) {
        return bookings.stream().anyMatch(booking -> {
            LocalDateTime bookingStart = booking.getBookingDate();
            int duration = booking.getDurationMinutes() == null || booking.getDurationMinutes() <= 0
                    ? 60 : booking.getDurationMinutes();
            LocalDateTime bookingEnd = booking.getBookingEndDate() == null
                    ? bookingStart.plusMinutes(duration)
                    : booking.getBookingEndDate();
            return bookingStart.isBefore(end) && bookingEnd.isAfter(start);
        });
    }

    private boolean overlapsTimeOff(LocalDateTime start, LocalDateTime end, List<ArtistTimeOff> blocks) {
        return blocks.stream().anyMatch(block ->
                block.getStartDateTime().isBefore(end) && block.getEndDateTime().isAfter(start)
        );
    }

    private Users findArtistByEmail(String email) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for email: " + email));
        boolean hasArtistRole = user.getRoles().stream().anyMatch(role -> role.getRole() == UserRole.ARTIST);
        if (!hasArtistRole) {
            throw new ConflictException("Artist role is required");
        }
        return user;
    }

    private ArtistAvailabilityResponse toAvailabilityResponse(ArtistAvailability availability) {
        return new ArtistAvailabilityResponse(
                availability.getId(),
                availability.getDayOfWeek(),
                availability.getStartTime(),
                availability.getEndTime(),
                availability.getSlotDurationMinutes(),
                availability.getIsActive()
        );
    }

    private ArtistTimeOffResponse toTimeOffResponse(ArtistTimeOff timeOff) {
        return new ArtistTimeOffResponse(
                timeOff.getId(),
                timeOff.getStartDateTime(),
                timeOff.getEndDateTime(),
                timeOff.getReason()
        );
    }
}
