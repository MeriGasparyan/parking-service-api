package com.merigasparyan.jmp.parkingserviceapi.service;

import com.merigasparyan.jmp.parkingserviceapi.dto.AvailableSpotDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.BookingDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.CreateBookingDTO;
import com.merigasparyan.jmp.parkingserviceapi.enums.BookingStatus;
import com.merigasparyan.jmp.parkingserviceapi.enums.SpotType;
import com.merigasparyan.jmp.parkingserviceapi.exception.BookingConflictException;
import com.merigasparyan.jmp.parkingserviceapi.exception.ResourceNotFoundException;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Booking;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Spot;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.User;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.BookingRepository;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.SpotRepository;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final SpotRepository spotRepository;
    private final UserRepository userRepository;

    @Value("${guest.booking.max.duration:120}")
    private Long maxDurationMinutes;

    private final List<BookingStatus> blockedStatuses = List.of(BookingStatus.RESERVED, BookingStatus.PARKED);

    /* -------------------- CREATE -------------------- */
    @Transactional
    public BookingDTO createBooking(CreateBookingDTO dto, Long userId) {
        Spot spot = getSpot(dto.getSpotId());
        User user = getUser(userId);

        validateBookingForUserSpot(spot, user);
        return createBooking(dto, spot, user);
    }

    @Transactional
    public BookingDTO createGuestBooking(CreateBookingDTO dto, Long userId) {
        Spot spot = getSpot(dto.getSpotId());
        User user = getUser(userId);

        if (spot.getSpotType() != SpotType.VISITOR) {
            throw new BookingConflictException("Guest bookings must be for visitor spots only.");
        }

        long duration = Duration.between(dto.getStartTime(), dto.getEndTime()).toMinutes();
        if (duration > maxDurationMinutes) {
            throw new BookingConflictException(
                    "Booking exceeds max duration of " + maxDurationMinutes + " minutes."
            );
        }

        return createBooking(dto, spot, user);
    }

    private void validateBookingForUserSpot(Spot spot, User user) {
        if (!spot.getCommunity().equals(user.getCommunity())) {
            throw new BookingConflictException("Spot is not in the same community.");
        }
        if (spot.getSpotType() == SpotType.VISITOR) {
            throw new BookingConflictException("Visitor spots are for guests only.");
        }
    }

    private BookingDTO createBooking(CreateBookingDTO dto, Spot spot, User user) {
        if (!isSpotAvailable(spot, dto.getStartTime(), dto.getEndTime())) {
            throw new BookingConflictException("Spot is already booked for the selected time.");
        }

        Booking booking = Booking.builder()
                .spot(spot)
                .user(user)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(BookingStatus.RESERVED)
                .build();

        return BookingDTO.mapToBookingDto(bookingRepository.save(booking));
    }

    /* -------------------- STATUS CHANGES -------------------- */
    @Transactional
    public BookingDTO updateBookingStatus(Long bookingId, BookingStatus newStatus, BookingStatus... allowedCurrent) {
        Booking booking = getBooking(bookingId);

        if (allowedCurrent.length > 0 && !List.of(allowedCurrent).contains(booking.getStatus())) {
            throw new IllegalStateException("Booking cannot transition from " + booking.getStatus() + " to " + newStatus);
        }

        booking.setStatus(newStatus);
        return BookingDTO.mapToBookingDto(bookingRepository.save(booking));
    }
    @Transactional
    public BookingDTO parkInSpot(Long bookingId) {
        return updateBookingStatus(bookingId, BookingStatus.PARKED, BookingStatus.RESERVED);
    }
    @Transactional
    public BookingDTO leaveSpot(Long bookingId) {
        return updateBookingStatus(bookingId, BookingStatus.VACATED, BookingStatus.PARKED);
    }
    @Transactional
    public BookingDTO releaseSpot(Long bookingId) {
        return updateBookingStatus(bookingId, BookingStatus.COMPLETED);
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
        if (booking.getStatus() != BookingStatus.RESERVED) {
            throw new IllegalStateException("Only reserved bookings can be cancelled.");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    /* -------------------- QUERIES -------------------- */
    public List<BookingDTO> getUserBookings(Long userId) {
        return bookingRepository.findBookingsForUser(userId).stream()
                .map(BookingDTO::mapToBookingDto).toList();
    }

    public List<BookingDTO> getCurrentBookings(Long userId) {
        Instant now = Instant.now();
        return bookingRepository.findUpcomingBookingsForUser(userId, now).stream()
                .map(BookingDTO::mapToBookingDto).toList();
    }

    public List<AvailableSpotDTO> getAvailableSpots(Instant from, Instant to, Long communityId) {
        return spotRepository.findByCommunityId(communityId).stream()
                .filter(spot -> isSpotAvailable(spot, from, to))
                .map(spot -> new AvailableSpotDTO(spot.getId(), spot.getCode(), spot.getSpotType(), from, to))
                .toList();
    }

    /* -------------------- HELPERS -------------------- */
    private Spot getSpot(Long spotId) {
        return spotRepository.findById(spotId)
                .orElseThrow(() -> new ResourceNotFoundException("Spot not found."));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found."));
    }

    private boolean isSpotAvailable(Spot spot, Instant from, Instant to) {
        return !bookingRepository.existsBySpotIdAndStatusInAndEndTimeAfterAndStartTimeBefore(
                spot.getId(), blockedStatuses, from, to
        );
    }
}
