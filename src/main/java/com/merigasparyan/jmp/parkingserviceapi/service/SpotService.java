package com.merigasparyan.jmp.parkingserviceapi.service;

import com.merigasparyan.jmp.parkingserviceapi.dto.CreateSpotDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.SpotDTO;
import com.merigasparyan.jmp.parkingserviceapi.exception.ResourceNotFoundException;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Community;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Spot;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.CommunityRepository;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpotService {
    private final SpotRepository spotRepository;
    private final CommunityRepository communityRepository;

    public SpotDTO createSpot(CreateSpotDTO spotDto) {
        Community community = communityRepository.findById(spotDto.getCommunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));

        Spot spot = new Spot();
        spot.setCode(spotDto.getCode());
        spot.setAddress(spotDto.getAddress());
        spot.setSpotType(spotDto.getSpotType());
        spot.setCommunity(community);

        spot = spotRepository.save(spot);
        return SpotDTO.mapToSpotDto(spot);
    }

    public SpotDTO getSpotById(Long id) {
        return spotRepository.findById(id)
                .map(SpotDTO::mapToSpotDto)
                .orElseThrow(() -> new ResourceNotFoundException("Spot not found"));
    }

    public SpotDTO updateSpot(Long id, CreateSpotDTO spotDto) {
        Spot spot = spotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spot not found"));

        Community community = communityRepository.findById(spotDto.getCommunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));

        spot.setCode(spotDto.getCode());
        spot.setAddress(spotDto.getAddress());
        spot.setSpotType(spotDto.getSpotType());
        spot.setCommunity(community);

        spot = spotRepository.save(spot);
        return SpotDTO.mapToSpotDto(spot);
    }

    public void deleteSpot(Long id) {
        spotRepository.deleteById(id);
    }

}
