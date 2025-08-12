package com.merigasparyan.jmp.parkingserviceapi.controller;

import com.merigasparyan.jmp.parkingserviceapi.dto.CreateSpotDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.SpotDTO;
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
        permissionChecker.checkPermission(user, "SPOT_CREATE");
        return new ResponseEntity<>(spotService.createSpot(dto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpotDTO> getSpotById(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long id
    ) {
        permissionChecker.checkPermission(user, "SPOT_VIEW");
        return ResponseEntity.ok(spotService.getSpotById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpotDTO> updateSpot(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long id,
            @RequestBody CreateSpotDTO dto
    ) {
        permissionChecker.checkPermission(user, "SPOT_UPDATE");
        return ResponseEntity.ok(spotService.updateSpot(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpot(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long id
    ) {
        permissionChecker.checkPermission(user, "SPOT_DELETE");
        spotService.deleteSpot(id);
        return ResponseEntity.noContent().build();
    }
}