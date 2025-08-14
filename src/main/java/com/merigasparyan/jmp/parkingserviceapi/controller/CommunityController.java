package com.merigasparyan.jmp.parkingserviceapi.controller;

import com.merigasparyan.jmp.parkingserviceapi.dto.*;
import com.merigasparyan.jmp.parkingserviceapi.enums.Permission;
import com.merigasparyan.jmp.parkingserviceapi.security.CustomUserDetails;
import com.merigasparyan.jmp.parkingserviceapi.security.PermissionChecker;
import com.merigasparyan.jmp.parkingserviceapi.service.BookingService;
import com.merigasparyan.jmp.parkingserviceapi.service.CommunityService;
import com.merigasparyan.jmp.parkingserviceapi.service.UserService;
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
@RequestMapping("/api/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final PermissionChecker permissionChecker;
    private final CommunityService communityService;
    private final UserService userService;
    private final BookingService bookingService;

    /* --------------------------------- SPOTS --------------------------------- */
    @GetMapping("/{communityId}/spots")
    public ResponseEntity<List<SpotDTO>> getAllSpotsByCommunity(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long communityId
    ) {
        checkPermission(user, Permission.VIEW_AVAILABLE_SPOT);
        return ResponseEntity.ok(communityService.getAllSpotsByCommunity(communityId));
    }

    @GetMapping("/{communityId}/spots/available")
    public ResponseEntity<List<AvailableSpotDTO>> getAvailableSpots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @PathVariable Long communityId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        checkPermission(user, Permission.VIEW_AVAILABLE_SPOT);
        return ResponseEntity.ok(bookingService.getAvailableSpots(from, to, communityId));
    }

    /* --------------------------------- USERS --------------------------------- */
    @PostMapping("/{communityId}/users")
    public ResponseEntity<UserDTO> createUser(
            @PathVariable Long communityId,
            @RequestBody @Valid CreateUserDTO dto
    ) {
        return new ResponseEntity<>(userService.createUser(dto, communityId), HttpStatus.CREATED);
    }

    /* --------------------------------- COMMUNITIES --------------------------------- */
    @GetMapping("/{communityId}")
    public ResponseEntity<CommunityDTO> getCommunity(@PathVariable Long communityId) {
        return ResponseEntity.ok(communityService.getCommunity(communityId));
    }

    @PostMapping
    public ResponseEntity<CommunityDTO> createCommunity(
            @RequestBody @Valid CreateCommunityDTO dto,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        checkPermission(user, Permission.CREATE_COMMUNITY);
        return new ResponseEntity<>(communityService.createCommunity(dto, user), HttpStatus.CREATED);
    }

    @PutMapping("/{communityId}")
    public ResponseEntity<CommunityDTO> updateCommunity(
            @PathVariable Long communityId,
            @RequestBody @Valid UpdateCommunityDTO dto,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        checkPermission(user, Permission.UPDATE_COMMUNITY);
        return ResponseEntity.ok(communityService.updateCommunity(communityId, user, dto));
    }

    @DeleteMapping("/{communityId}")
    public ResponseEntity<Void> deleteCommunity(
            @PathVariable Long communityId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        checkPermission(user, Permission.DELETE_COMMUNITY);
        communityService.deleteCommunity(communityId, user);
        return ResponseEntity.noContent().build();
    }

    /* --------------------------------- HELPER --------------------------------- */
    private void checkPermission(CustomUserDetails user, Permission permission) {
        permissionChecker.checkPermission(user, List.of(permission.name()));
    }
}
