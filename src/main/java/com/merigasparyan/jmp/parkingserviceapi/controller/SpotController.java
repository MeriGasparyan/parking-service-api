package com.merigasparyan.jmp.parkingserviceapi.controller;

import com.merigasparyan.jmp.parkingserviceapi.dto.CreateSpotDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.SpotDTO;
import com.merigasparyan.jmp.parkingserviceapi.enums.Permission;
import com.merigasparyan.jmp.parkingserviceapi.security.CustomUserDetails;
import com.merigasparyan.jmp.parkingserviceapi.security.PermissionChecker;
import com.merigasparyan.jmp.parkingserviceapi.service.SpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spots")
@RequiredArgsConstructor
public class SpotController {
    private final PermissionChecker permissionChecker;
    private final SpotService spotService;

    @PostMapping
    public ResponseEntity<SpotDTO> createSpot(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestBody CreateSpotDTO dto
    ) {
        System.out.println(permissionChecker.getPermissionsForUser(user.getId()));
       // permissionChecker.checkPermission(user, List.of(Permission.CREATE_SPOT.name()));
        return new ResponseEntity<>(spotService.createSpot(dto, user), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpotDTO> getSpotById(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long id
    ) {
        permissionChecker.checkPermission(user, List.of(Permission.VIEW_AVAILABLE_SPOT.name()));
        return ResponseEntity.ok(spotService.getSpotById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpotDTO> updateSpot(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long id,
            @RequestBody CreateSpotDTO dto
    ) {
        permissionChecker.checkPermission(user, List.of(Permission.UPDATE_SPOT.name()));
        return ResponseEntity.ok(spotService.updateSpot(id, dto, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpot(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long id
    ) {
        permissionChecker.checkPermission(user, List.of(Permission.DELETE_SPOT.name()));
        spotService.deleteSpot(id, user);
        return ResponseEntity.noContent().build();
    }
}