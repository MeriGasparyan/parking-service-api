package com.merigasparyan.jmp.parkingserviceapi.service;

import com.merigasparyan.jmp.parkingserviceapi.dto.CommunityDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.CreateCommunityDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.SpotDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.UpdateCommunityDTO;
import com.merigasparyan.jmp.parkingserviceapi.enums.Role;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Community;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.User;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.CommunityRepository;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.SpotRepository;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.UserRepository;
import com.merigasparyan.jmp.parkingserviceapi.security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final SpotRepository spotRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<SpotDTO> getAllSpotsByCommunity(Long communityId) {
        return spotRepository.findByCommunityId(communityId).stream()
                .map(SpotDTO::mapToSpotDto)
                .toList();
    }

    @Transactional
    public CommunityDTO createCommunity(CreateCommunityDTO dto, CustomUserDetails currentUser) {
        if (!currentUser.getUser().getRole().getRole().equals(Role.ROLE_ADMIN))
            throw new IllegalCallerException("User is not admin");
        Community community = new Community();
        community.setName(dto.getName());
        community.setAddress(dto.getAddress());
        if(dto.getManagerId() != null){
            User manager = userRepository.findById(dto.getManagerId()).orElse(null);
            if (manager != null) {
                community.setCommunityManager(manager);
            }
        }

        Community saved = communityRepository.save(community);
        return CommunityDTO.mapToDTO(community);
    }

    @Transactional
    public CommunityDTO updateCommunity(Long id, CustomUserDetails currentUser, UpdateCommunityDTO dto) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id " + id));

        if ((community.getCommunityManager() != null &&
                !community.getCommunityManager().getId().equals(currentUser.getId()) )
                || !currentUser.getUser().getRole().getRole().equals(Role.ROLE_ADMIN)) {
            throw new IllegalCallerException("Community update is not allowed");
        }
        if (dto.getName() != null) {
            community.setName(dto.getName());
        }
        if (dto.getAddress() != null) {
            community.setAddress(dto.getAddress());
        }
        if (dto.getManagerId() != null) {
            User manager = userRepository.findById(dto.getManagerId())
                    .orElseThrow(() -> new EntityNotFoundException("Community manager not found with id " + dto.getManagerId()));
            community.setCommunityManager(manager);
        }

        Community updated = communityRepository.save(community);
        return CommunityDTO.mapToDTO(community);
    }

    @Transactional
    public CommunityDTO getCommunity(Long id) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id " + id));
        return CommunityDTO.mapToDTO(community);
    }

    @Transactional
    public List<CommunityDTO> getAllCommunities() {
        return communityRepository.findAll().stream()
                .map(CommunityDTO::mapToDTO)
                .toList();
    }

    @Transactional
    public void deleteCommunity(Long id, CustomUserDetails currentUser) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id " + id));

        if ((community.getCommunityManager() != null &&
                !community.getCommunityManager().getId().equals(currentUser.getId()) )|| !currentUser.getUser().getRole().getRole().equals(Role.ROLE_ADMIN)) {
            throw new IllegalCallerException("Community deletion is not allowed");
        }
        communityRepository.deleteById(id);
    }

}
