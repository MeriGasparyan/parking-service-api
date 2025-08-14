package com.merigasparyan.jmp.parkingserviceapi.controller;

import com.merigasparyan.jmp.parkingserviceapi.dto.AvailableSpotDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.BookingDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.CreateBookingDTO;
import com.merigasparyan.jmp.parkingserviceapi.enums.Permission;
import com.merigasparyan.jmp.parkingserviceapi.enums.BookingStatus;
import com.merigasparyan.jmp.parkingserviceapi.security.CustomUserDetails;
import com.merigasparyan.jmp.parkingserviceapi.security.PermissionChecker;
import com.merigasparyan.jmp.parkingserviceapi.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final PermissionChecker permissionChecker;

    /* -------------------- CREATE -------------------- */
    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid CreateBookingDTO dto
    ) {
        permissionChecker.checkPermission(user, List.of(Permission.CREATE_BOOKING.name()));
        return new ResponseEntity<>(bookingService.createBooking(dto, user.getId()), HttpStatus.CREATED);
    }

    @PostMapping("/guest")
    public ResponseEntity<BookingDTO> createGuestBooking(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody @Valid CreateBookingDTO dto
    ) {
        permissionChecker.checkPermission(user, List.of(Permission.CREATE_BOOKING.name()));
        return new ResponseEntity<>(bookingService.createGuestBooking(dto, user.getId()), HttpStatus.CREATED);
    }

    /* -------------------- STATUS -------------------- */
    @PatchMapping("/{id}/park")
    public ResponseEntity<BookingDTO> park(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.parkInSpot(id));
    }

    @PatchMapping("/{id}/leave")
    public ResponseEntity<BookingDTO> leave(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.leaveSpot(id));
    }

    @PatchMapping("/{id}/release")
    public ResponseEntity<BookingDTO> release(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.releaseSpot(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingDTO> changeStatus(@PathVariable Long id,
                                                   @RequestParam BookingStatus status) {
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id,
                                              @AuthenticationPrincipal CustomUserDetails user) {
        bookingService.cancelBooking(id, user.getId());
        return ResponseEntity.noContent().build();
    }

    /* -------------------- QUERIES -------------------- */
    @GetMapping("/user")
    public ResponseEntity<List<BookingDTO>> getUserBookings(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(bookingService.getUserBookings(user.getId()));
    }

    @GetMapping("/user/current")
    public ResponseEntity<List<BookingDTO>> getCurrentBookings(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(bookingService.getCurrentBookings(user.getId()));
    }

    @GetMapping("/available")
    public ResponseEntity<List<AvailableSpotDTO>> getAvailableSpots(
            @RequestParam Long communityId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to
    ) {
        return ResponseEntity.ok(bookingService.getAvailableSpots(from, to, communityId));
    }
}
