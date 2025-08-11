package com.merigasparyan.jmp.parkingserviceapi.controller;

import com.merigasparyan.jmp.parkingserviceapi.dto.CreateSpotDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.SpotDTO;
import com.merigasparyan.jmp.parkingserviceapi.service.SpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/spots")
@RequiredArgsConstructor
public class SpotController {

    private final SpotService spotService;

    @PostMapping
    public ResponseEntity<SpotDTO> createSpot(@RequestBody CreateSpotDTO dto) {
        return new ResponseEntity<>(spotService.createSpot(dto), HttpStatus.CREATED);
    }

    @GetMapping("/community/{communityId}")
    public ResponseEntity<List<SpotDTO>> getAllSpotsByCommunity(@PathVariable Long communityId) {
        return ResponseEntity.ok(spotService.getAllSpotsByCommunity(communityId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpotDTO> getSpotById(@PathVariable Long id) {
        return ResponseEntity.ok(spotService.getSpotById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpotDTO> updateSpot(@PathVariable Long id, @RequestBody CreateSpotDTO dto) {
        return ResponseEntity.ok(spotService.updateSpot(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpot(@PathVariable Long id) {
        spotService.deleteSpot(id);
        return ResponseEntity.noContent().build();
    }
}