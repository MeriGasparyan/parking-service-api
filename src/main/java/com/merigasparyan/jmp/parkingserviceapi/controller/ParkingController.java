package com.merigasparyan.jmp.parkingserviceapi.controller;

import com.merigasparyan.jmp.parkingserviceapi.dto.AvailableSpotDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.BookingDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.CreateBookingDTO;
import com.merigasparyan.jmp.parkingserviceapi.enums.Permission;
import com.merigasparyan.jmp.parkingserviceapi.security.CustomUserDetails;
import com.merigasparyan.jmp.parkingserviceapi.security.PermissionChecker;
import com.merigasparyan.jmp.parkingserviceapi.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/parking")
@RequiredArgsConstructor
public class ParkingController {
    private final PermissionChecker permissionChecker;
    private final ParkingService parkingService;

    @PostMapping("/book")
    public ResponseEntity<BookingDTO> createBooking(
            @RequestBody CreateBookingDTO dto,
            @RequestParam Long userId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        permissionChecker.checkPermission(currentUser, Permission.CREATE_BOOKING.name());
        return ResponseEntity.ok(parkingService.createBooking(dto, userId));
    }

    @PostMapping("/guest-book")
    public ResponseEntity<BookingDTO> createGuestBooking(
            @RequestBody CreateBookingDTO dto,
            @RequestParam Long userId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        permissionChecker.checkPermission(currentUser, Permission.CREATE_GUEST_BOOKING.name());
        return ResponseEntity.ok(parkingService.createGuestBooking(dto, userId));
    }

    @GetMapping("/user-bookings/{userId}")
    public ResponseEntity<List<BookingDTO>> getUserBookings(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        permissionChecker.checkPermission(currentUser, Permission.VIEW_ALL_BOOKINGS.name(),
                Permission.VIEW_OWN_BOOKING.name());
        return ResponseEntity.ok(parkingService.getUserBookings(userId, currentUser.getId()));
    }

    @GetMapping("/current-bookings")
    public ResponseEntity<List<BookingDTO>> getCurrentBookings(
            @RequestParam Long userId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        permissionChecker.checkPermission(currentUser, Permission.VIEW_CURRENT_BOOKINGS.name());
        return ResponseEntity.ok(parkingService.getCurrentBookings(userId));
    }

    @GetMapping("/{communityId}/available-spots")
    public ResponseEntity<List<AvailableSpotDTO>> getAvailableSpots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @PathVariable Long communityId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        permissionChecker.checkPermission(currentUser, Permission.VIEW_AVAILABLE_SPOT.name());
        return ResponseEntity.ok(parkingService.getAvailableSpots(from, to, communityId));
    }

    @PostMapping("/park/{bookingId}")
    public ResponseEntity<BookingDTO> parkInSpot(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        permissionChecker.checkPermission(currentUser, Permission.PARK_IN_SPOT.name());
        return ResponseEntity.ok(parkingService.parkInSpot(bookingId));
    }

    @PostMapping("/leave/{bookingId}")
    public ResponseEntity<BookingDTO> leaveParking(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        permissionChecker.checkPermission(currentUser, Permission.PARK_IN_SPOT.name());
        return ResponseEntity.ok(parkingService.leaveTheParking(bookingId));
    }

    @PostMapping("/release/{bookingId}")
    public ResponseEntity<BookingDTO> releaseSpot(
            @PathVariable Long bookingId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        permissionChecker.checkPermission(currentUser, Permission.RELEASE_SPOT.name());
        return ResponseEntity.ok(parkingService.releaseSpot(bookingId));
    }

    @PostMapping("/cancel/{bookingId}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long bookingId,
            @RequestParam Long userId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        permissionChecker.checkPermission(currentUser, Permission.CANCEL_BOOKING.name());
        parkingService.cancelBooking(bookingId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-status/{bookingId}")
    public ResponseEntity<BookingDTO> changeBookingStatus(
            @PathVariable Long bookingId,
            @RequestParam String status,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        permissionChecker.checkPermission(currentUser, Permission.CHANGE_BOOKING_STATUS.name());
        return ResponseEntity.ok(parkingService.changeBookingStatus(bookingId, status));
    }
}
