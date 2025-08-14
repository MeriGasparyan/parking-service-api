package com.merigasparyan.jmp.parkingserviceapi.controller;

import com.merigasparyan.jmp.parkingserviceapi.dto.BookingDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.CreateUserDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.UpdateUserDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.UserDTO;
import com.merigasparyan.jmp.parkingserviceapi.enums.Permission;
import com.merigasparyan.jmp.parkingserviceapi.security.CustomUserDetails;
import com.merigasparyan.jmp.parkingserviceapi.security.PermissionChecker;
import com.merigasparyan.jmp.parkingserviceapi.service.BookingService;
import com.merigasparyan.jmp.parkingserviceapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PermissionChecker permissionChecker;
    private final BookingService bookingService;

    /* ---------------------- CREATE ---------------------- */
    @PostMapping("/admin")
    public ResponseEntity<UserDTO> createAdmin(@RequestBody @Valid CreateUserDTO dto) {
        return new ResponseEntity<>(userService.createAdmin(dto), HttpStatus.CREATED);
    }

    /* ---------------------- READ ---------------------- */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /* ---------------------- UPDATE ---------------------- */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserDTO dto,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(userService.updateUser(id, currentUser.getId(), dto));
    }

    /* ---------------------- DELETE ---------------------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        userService.deleteUser(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    /* ---------------------- BOOKINGS ---------------------- */
    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDTO>> getUserBookings(@AuthenticationPrincipal CustomUserDetails currentUser) {
        permissionChecker.checkPermission(currentUser, List.of(Permission.VIEW_ALL_BOOKINGS.name(),
                Permission.VIEW_OWN_BOOKING.name()));
        return ResponseEntity.ok(bookingService.getUserBookings(currentUser.getId()));
    }

    @GetMapping("/bookings/current")
    public ResponseEntity<List<BookingDTO>> getCurrentBookings(@AuthenticationPrincipal CustomUserDetails currentUser) {
        permissionChecker.checkPermission(currentUser, List.of(Permission.VIEW_CURRENT_BOOKINGS.name()));
        return ResponseEntity.ok(bookingService.getCurrentBookings(currentUser.getId()));
    }
}
