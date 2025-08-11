package com.merigasparyan.jmp.parkingserviceapi.service;

import com.merigasparyan.jmp.parkingserviceapi.dto.CommunityDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.CreateCommunityDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.UpdateCommunityDTO;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Community;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.User;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.CommunityRepository;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommunityDTO createCommunity(CreateCommunityDTO dto) {
        User manager = userRepository.findById(dto.getManagerId())
                .orElseThrow(() -> new EntityNotFoundException("Community manager not found with id " + dto.getManagerId()));

        Community community = new Community();
        community.setName(dto.getName());
        community.setAddress(dto.getAddress());
        community.setCommunityManager(manager);

        Community saved = communityRepository.save(community);
        return CommunityDTO.mapToDTO(community);
    }

    @Transactional
    public CommunityDTO updateCommunity(Long id, UpdateCommunityDTO dto) {
        Community community = communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id " + id));

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
    public void deleteCommunity(Long id) {
        if (!communityRepository.existsById(id)) {
            throw new EntityNotFoundException("Community not found with id " + id);
        }
        communityRepository.deleteById(id);
    }

}
