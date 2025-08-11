package com.merigasparyan.jmp.parkingserviceapi.controller;

import com.merigasparyan.jmp.parkingserviceapi.dto.AvailableSpotDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.BookingDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.CreateBookingDTO;
import com.merigasparyan.jmp.parkingserviceapi.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class ParkingController {

    private final ParkingService parkingService;

    @PostMapping("/book")
    public ResponseEntity<BookingDTO> createBooking(@RequestBody CreateBookingDTO dto, @RequestParam Long userId) {
        return ResponseEntity.ok(parkingService.createBooking(dto, userId));
    }

    @PostMapping("/guest-book")
    public ResponseEntity<BookingDTO> createGuestBooking(@RequestBody CreateBookingDTO dto, @RequestParam Long userId) {
        return ResponseEntity.ok(parkingService.createGuestBooking(dto, userId));
    }

    @GetMapping("/user-bookings/{userId}")
    public ResponseEntity<List<BookingDTO>> getUserBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(parkingService.getUserBookings(userId));
    }

    @GetMapping("/available-spots")
    public ResponseEntity<List<AvailableSpotDTO>> getAvailableSpots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam Long communityId) {
        return ResponseEntity.ok(parkingService.getAvailableSpots(from, to, communityId));
    }

    @PostMapping("/park/{bookingId}")
    public ResponseEntity<BookingDTO> parkInSpot(@PathVariable Long bookingId) {
        return ResponseEntity.ok(parkingService.parkInSpot(bookingId));
    }

    @PostMapping("/leave/{bookingId}")
    public ResponseEntity<BookingDTO> leaveParking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(parkingService.leaveTheParking(bookingId));
    }

    @PostMapping("/release/{bookingId}")
    public ResponseEntity<BookingDTO> releaseSpot(@PathVariable Long bookingId) {
        return ResponseEntity.ok(parkingService.releaseSpot(bookingId));
    }

    @PostMapping("/cancel/{bookingId}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId, @RequestParam Long userId) {
        parkingService.cancelBooking(bookingId, userId);
        return ResponseEntity.noContent().build();
    }
}
