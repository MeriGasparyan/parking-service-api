package com.merigasparyan.jmp.parkingserviceapi.controller;

import com.merigasparyan.jmp.parkingserviceapi.dto.*;
import com.merigasparyan.jmp.parkingserviceapi.enums.Permission;
import com.merigasparyan.jmp.parkingserviceapi.security.CustomUserDetails;
import com.merigasparyan.jmp.parkingserviceapi.security.PermissionChecker;
import com.merigasparyan.jmp.parkingserviceapi.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
public class CommunityController {
    private final PermissionChecker permissionChecker;
    private final CommunityService communityService;

    @GetMapping("/{communityId}/spots")
    public ResponseEntity<List<SpotDTO>> getAllSpotsByCommunity(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long communityId
    ) {
        permissionChecker.checkPermission(user, List.of(Permission.VIEW_AVAILABLE_SPOT.name()));
        return ResponseEntity.ok(communityService.getAllSpotsByCommunity(communityId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<CommunityDTO> getCommunity(@PathVariable Long id) {
        return ResponseEntity.ok(communityService.getCommunity(id));
    }
    @PostMapping
    public ResponseEntity<CommunityDTO> createCommunity(
            @RequestBody CreateCommunityDTO dto,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        permissionChecker.checkPermission(currentUser, List.of(Permission.CREATE_COMMUNITY.name()));
        return new ResponseEntity<>(communityService.createCommunity(dto,currentUser), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommunityDTO> updateCommunity(
            @PathVariable Long id,
            @RequestBody UpdateCommunityDTO dto,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        permissionChecker.checkPermission(currentUser, List.of(Permission.UPDATE_COMMUNITY.name()));
        return ResponseEntity.ok(communityService.updateCommunity(id, currentUser, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunity(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        permissionChecker.checkPermission(currentUser, List.of(Permission.DELETE_COMMUNITY.name()));
        communityService.deleteCommunity(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}