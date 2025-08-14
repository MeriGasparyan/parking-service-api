package com.merigasparyan.jmp.parkingserviceapi.controller;

import com.merigasparyan.jmp.parkingserviceapi.dto.CreateSpotDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.SpotDTO;
import com.merigasparyan.jmp.parkingserviceapi.enums.Permission;
import com.merigasparyan.jmp.parkingserviceapi.security.CustomUserDetails;
import com.merigasparyan.jmp.parkingserviceapi.security.PermissionChecker;
import com.merigasparyan.jmp.parkingserviceapi.service.SpotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communities/{communityId}/spots")
@RequiredArgsConstructor
public class SpotController {

    private final SpotService spotService;
    private final PermissionChecker permissionChecker;

    /* ---------------------- CREATE ---------------------- */
    @PostMapping
    public ResponseEntity<SpotDTO> createSpot(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long communityId,
            @RequestBody @Valid CreateSpotDTO dto
    ) {
        checkPermission(user, Permission.CREATE_SPOT);
        return new ResponseEntity<>(spotService.createSpot(dto, user, communityId), HttpStatus.CREATED);
    }

    /* ---------------------- READ ---------------------- */
    @GetMapping("/{id}")
    public ResponseEntity<SpotDTO> getSpotById(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long id
    ) {
        checkPermission(user, Permission.VIEW_AVAILABLE_SPOT);
        return ResponseEntity.ok(spotService.getSpotById(id));
    }

    /* ---------------------- UPDATE ---------------------- */
    @PutMapping("/{id}")
    public ResponseEntity<SpotDTO> updateSpot(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long id,
            @RequestBody @Valid CreateSpotDTO dto
    ) {
        checkPermission(user, Permission.UPDATE_SPOT);
        return ResponseEntity.ok(spotService.updateSpot(id, dto, user));
    }

    /* ---------------------- DELETE ---------------------- */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpot(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long id
    ) {
        checkPermission(user, Permission.DELETE_SPOT);
        spotService.deleteSpot(id, user);
        return ResponseEntity.noContent().build();
    }

    /* ---------------------- HELPER ---------------------- */
    private void checkPermission(CustomUserDetails user, Permission permission) {
        permissionChecker.checkPermission(user, List.of(permission.name()));
    }
}
