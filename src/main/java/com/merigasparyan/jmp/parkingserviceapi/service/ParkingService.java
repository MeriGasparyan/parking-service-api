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
public class ParkingService {
    private final BookingRepository bookingRepository;
    private final SpotRepository spotRepository;
    private final UserRepository userRepository;

    @Value("${guest.booking.max.duration:120}")
    private Long maxDuration;

    @Transactional
    public BookingDTO createBooking(CreateBookingDTO bookingDto, Long userId) {
        Spot spot = spotRepository.findById(bookingDto.getSpotId())
                .orElseThrow(() -> new ResourceNotFoundException("Spot not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (spot.getCommunity() != user.getCommunity()) {
            throw new BookingConflictException("Spot is not in community, please use guest booking service.");
        }
        if (spot.getSpotType() == SpotType.VISITOR)
            throw new BookingConflictException("Visitor spot should only be occupied by visitors");

        return getBookingDTO(bookingDto, spot, user);
    }

    private BookingDTO getBookingDTO(CreateBookingDTO bookingDto, Spot spot, User user) {
        boolean hasOverlap = bookingRepository.existsBySpotIdAndStatusInAndEndTimeAfterAndStartTimeBefore(
                spot.getId(),
                List.of(BookingStatus.BOOKED, BookingStatus.IN_PROGRESS, BookingStatus.PARKED),
                bookingDto.getStartTime(),
                bookingDto.getEndTime());

        if (hasOverlap) {
            throw new BookingConflictException("The spot is already booked for the selected time range");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setStartTime(bookingDto.getStartTime());
        booking.setEndTime(bookingDto.getEndTime());
        booking.setStatus(BookingStatus.BOOKED);
        booking = bookingRepository.save(booking);

        return BookingDTO.mapToBookingDto(booking);
    }

    public BookingDTO createGuestBooking(CreateBookingDTO bookingDto, Long userId) {


        Spot spot = spotRepository.findById(bookingDto.getSpotId())
                .orElseThrow(() -> new ResourceNotFoundException("Spot not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (spot.getSpotType() != SpotType.VISITOR)
            throw new BookingConflictException("A visitor should use a guest spot");

        if (Duration.between(bookingDto.getStartTime(),
                bookingDto.getEndTime()).toMillis() >= maxDuration)
            throw new BookingConflictException("The visitor spot cannot be booked due to a duration limit of " + maxDuration);
        return getBookingDTO(bookingDto, spot, user);
    }

    public List<BookingDTO> getUserBookings(Long userId, Long currentUserId) {
        if(!currentUserId.equals(userId)) {
            throw new IllegalCallerException("You cannot view other user's bookings");
        }
        return bookingRepository.findBookingsForUser(userId).stream()
                .map(BookingDTO::mapToBookingDto)
                .toList();
    }

    public List<AvailableSpotDTO> getAvailableSpots(Instant from, Instant to, Long communityId) {
        List<Spot> allSpots = spotRepository.findByCommunityId(communityId);

        return allSpots.stream()
                .filter(spot -> isSpotAvailable(spot, from, to))
                .map(spot -> {
                    AvailableSpotDTO dto = new AvailableSpotDTO();
                    dto.setSpotId(spot.getId());
                    dto.setCode(spot.getCode());
                    dto.setSpotType(spot.getSpotType());
                    dto.setAvailableFrom(from);
                    dto.setAvailableTo(to);
                    return dto;
                })
                .toList();
    }

    @Transactional
    public BookingDTO parkInSpot(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getStatus().equals(BookingStatus.BOOKED)) {
            throw new IllegalStateException("Only confirmed bookings can be parked");
        }
        booking.setStatus(BookingStatus.PARKED);
        bookingRepository.save(booking);

        return BookingDTO.mapToBookingDto(booking);
    }

    @Transactional
    public BookingDTO leaveTheParking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getStatus().equals(BookingStatus.PARKED)) {
            throw new IllegalStateException("Only parked bookings can be leaving a parking");
        }
        booking.setStatus(BookingStatus.IN_PROGRESS);
        bookingRepository.save(booking);

        return BookingDTO.mapToBookingDto(booking);
    }

    @Transactional
    public BookingDTO releaseSpot(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        return BookingDTO.mapToBookingDto(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getStatus().equals(BookingStatus.BOOKED)) {
            throw new IllegalStateException("Only confirmed bookings can be cancelled");
        }


        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);


    }

    public List<BookingDTO> getCurrentBookings(Long userId) {
        Instant now = Instant.now();
        return bookingRepository.findUpcomingBookingsForUser(userId, now).stream()
                .map(BookingDTO::mapToBookingDto)
                .toList();
    }

    public List<BookingDTO> getCurrentBookings() {
        return bookingRepository.findByStatusIn(List.of(BookingStatus.IN_PROGRESS, BookingStatus.PARKED)).stream()
                .map(BookingDTO::mapToBookingDto)
                .toList();
    }

    @Transactional
    public BookingDTO changeBookingStatus(Long bookingId, String status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        BookingStatus newStatus;
        try {
            newStatus = BookingStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid booking status: " + status);
        }
        booking.setStatus(newStatus);
        bookingRepository.save(booking);
        return BookingDTO.mapToBookingDto(booking);
    }
    private boolean isSpotAvailable(Spot spot, Instant from, Instant to) {
        return !bookingRepository.existsBySpotIdAndStatusInAndEndTimeAfterAndStartTimeBefore(
                spot.getId(),
                List.of(BookingStatus.IN_PROGRESS, BookingStatus.BOOKED),
                from,
                to);
    }
}