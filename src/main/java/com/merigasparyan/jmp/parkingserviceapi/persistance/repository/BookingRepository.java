package com.merigasparyan.jmp.parkingserviceapi.persistance.repository;

import com.merigasparyan.jmp.parkingserviceapi.enums.BookingStatus;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findById(Long id);

    @Query("SELECT b FROM Booking b WHERE b.id = :spotId AND b.status IN :statuses AND " +
            "((b.startTime BETWEEN :start AND :end) OR " +
            "(b.endTime BETWEEN :start AND :end) OR " +
            "(b.startTime <= :start AND b.endTime >= :end))")
    List<Booking> findOverlappingBookings(
            @Param("spotId") Long spotId,
            @Param("statuses") List<BookingStatus> statuses,
            @Param("start") Instant start,
            @Param("end") Instant end);

    default boolean existsBySpotIdAndStatusInAndEndTimeAfterAndStartTimeBefore(
            Long spotId,
            List<BookingStatus> statuses,
            Instant startTime,
            Instant endTime) {
        return false;
    }

    @Query("SELECT b FROM Booking b WHERE b.spot.community.id = :communityId AND " +
            "b.status IN ('BOOKED', 'IN_PROGRESS','PARKED') AND b.endTime > :now")
    List<Booking> findActiveBookingsByCommunity(
            @Param("communityId") Long communityId,
            @Param("now") Instant now);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId AND " +
            "b.status IN ('BOOKED', 'IN_PROGRESS','PARKED') AND b.endTime > :now")
    List<Booking> findUpcomingBookingsForUser(
            @Param("userId") Long userId,
            @Param("now") Instant now);

    @Query("SELECT b FROM Booking b WHERE b.user.id = :userId")
    List<Booking> findBookingsForUser(
            @Param("userId") Long userId
    );

    Optional<Booking> findByIdAndUserId(Long bookingId, Long userId);

    @Query("SELECT b FROM Booking b WHERE b.status IN :booked")
    List<Booking> findByStatusIn(List<BookingStatus> booked);
}
