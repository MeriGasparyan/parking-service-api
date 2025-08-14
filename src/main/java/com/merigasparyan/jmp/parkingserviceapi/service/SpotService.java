package com.merigasparyan.jmp.parkingserviceapi.service;

import com.merigasparyan.jmp.parkingserviceapi.dto.CreateSpotDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.SpotDTO;
import com.merigasparyan.jmp.parkingserviceapi.exception.ResourceNotFoundException;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Community;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Spot;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.CommunityRepository;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.SpotRepository;
import com.merigasparyan.jmp.parkingserviceapi.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpotService {

    private final SpotRepository spotRepository;
    private final CommunityRepository communityRepository;

    /* ---------------------- CREATE / UPDATE HELPER ---------------------- */
    private void validateCommunityManager(Community community, CustomUserDetails user) {
        if (community.getCommunityManager() == null || !community.getCommunityManager().getId().equals(user.getId())) {
            throw new IllegalCallerException("You are not allowed to manage spots in this community");
        }
    }

    /* ---------------------- CREATE ---------------------- */
    public SpotDTO createSpot(CreateSpotDTO dto, CustomUserDetails user, Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));
        validateCommunityManager(community, user);

        Spot spot = Spot.builder()
                .code(dto.getCode())
                .address(dto.getAddress())
                .spotType(dto.getSpotType())
                .community(community)
                .build();

        return SpotDTO.mapToSpotDto(spotRepository.save(spot));
    }

    /* ---------------------- READ ---------------------- */
    public SpotDTO getSpotById(Long id) {
        return spotRepository.findById(id)
                .map(SpotDTO::mapToSpotDto)
                .orElseThrow(() -> new ResourceNotFoundException("Spot not found"));
    }

    /* ---------------------- UPDATE ---------------------- */
    public SpotDTO updateSpot(Long id, CreateSpotDTO dto, CustomUserDetails user) {
        Spot spot = spotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spot not found"));
        validateCommunityManager(spot.getCommunity(), user);

        if (dto.getCode() != null) spot.setCode(dto.getCode());
        if (dto.getAddress() != null) spot.setAddress(dto.getAddress());
        if (dto.getSpotType() != null) spot.setSpotType(dto.getSpotType());

        return SpotDTO.mapToSpotDto(spotRepository.save(spot));
    }

    /* ---------------------- DELETE ---------------------- */
    public void deleteSpot(Long id, CustomUserDetails user) {
        Spot spot = spotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spot not found"));
        validateCommunityManager(spot.getCommunity(), user);
        spotRepository.deleteById(id);
    }
}
