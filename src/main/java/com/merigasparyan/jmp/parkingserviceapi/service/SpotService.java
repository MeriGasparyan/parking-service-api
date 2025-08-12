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

    public SpotDTO createSpot(CreateSpotDTO spotDto, CustomUserDetails user) {
        Community community = communityRepository.findById(spotDto.getCommunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Community not found"));
        if(community.getCommunityManager() == null || !community.getCommunityManager().getId().equals(user.getId())) {
            throw new IllegalCallerException("You cannot create a spot in this community");
        }
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

    public SpotDTO updateSpot(Long id, CreateSpotDTO spotDto, CustomUserDetails user) {
        Spot spot = spotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spot not found"));

        if(spot.getCommunity().getCommunityManager() == null || !spot.getCommunity().getCommunityManager().getId().equals(user.getId())) {
            throw new IllegalCallerException("You cannot update a spot in this community");
        }


        if (spotDto.getCode() != null) {
            spot.setCode(spotDto.getCode());
        }

        if (spotDto.getAddress() != null) {
            spot.setAddress(spotDto.getAddress());
        }

        if (spotDto.getSpotType() != null) {
            spot.setSpotType(spotDto.getSpotType());
        }

        if (spotDto.getCommunityId() != null &&
                !spotDto.getCommunityId().equals(spot.getCommunity().getId())) {
            Community community = communityRepository.findById(spotDto.getCommunityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Community not found"));
            spot.setCommunity(community);
        }

        return SpotDTO.mapToSpotDto(spotRepository.save(spot));
    }

    public void deleteSpot(Long id, CustomUserDetails user) {
        Spot spot = spotRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Spot not found"));
        if(!spot.getCommunity().getCommunityManager().getId().equals(user.getId())) {
            throw new IllegalCallerException("You cannot delete a spot in this community");
        }
        spotRepository.deleteById(id);
    }

}
