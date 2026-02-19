package com.artnest.repository;

import com.artnest.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomer_IdOrderByBookingDateDesc(Long customerId);

    List<Booking> findByArtist_IdOrderByBookingDateDesc(Long artistId);

    Optional<Booking> findByIdAndCustomer_Id(Long bookingId, Long customerId);

    Optional<Booking> findByIdAndArtist_Id(Long bookingId, Long artistId);
}
