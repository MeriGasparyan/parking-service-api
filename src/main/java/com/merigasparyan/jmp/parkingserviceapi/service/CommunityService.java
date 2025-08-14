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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final SpotRepository spotRepository;
    private final UserRepository userRepository;

    /* --------------------------------- SPOTS --------------------------------- */
    @Transactional
    public List<SpotDTO> getAllSpotsByCommunity(Long communityId) {
        return spotRepository.findByCommunityId(communityId)
                .stream()
                .map(SpotDTO::mapToSpotDto)
                .toList();
    }

    /* --------------------------------- COMMUNITIES --------------------------------- */
    @Transactional
    public CommunityDTO createCommunity(CreateCommunityDTO dto, CustomUserDetails currentUser) {
        requireAdmin(currentUser);

        Community community = new Community();
        community.setName(dto.getName());
        community.setAddress(dto.getAddress());
        community.setCommunityManager(findUserOrNull(dto.getManagerId()));

        Community saved = communityRepository.save(community);
        return CommunityDTO.mapToDTO(saved);
    }

    @Transactional
    public CommunityDTO updateCommunity(Long id, CustomUserDetails currentUser, UpdateCommunityDTO dto) {
        Community community = findCommunityOrThrow(id);
        requireAdminOrManager(currentUser, community);

        if (dto.getName() != null) community.setName(dto.getName());
        if (dto.getAddress() != null) community.setAddress(dto.getAddress());
        if (dto.getManagerId() != null) {
            community.setCommunityManager(findUserOrThrow(dto.getManagerId()));
        }

        return CommunityDTO.mapToDTO(communityRepository.save(community));
    }

    @Transactional
    public CommunityDTO getCommunity(Long id) {
        return CommunityDTO.mapToDTO(findCommunityOrThrow(id));
    }

    @Transactional
    public List<CommunityDTO> getAllCommunities() {
        return communityRepository.findAll()
                .stream()
                .map(CommunityDTO::mapToDTO)
                .toList();
    }

    @Transactional
    public void deleteCommunity(Long id, CustomUserDetails currentUser) {
        Community community = findCommunityOrThrow(id);
        requireAdminOrManager(currentUser, community);
        communityRepository.deleteById(id);
    }

    /* --------------------------------- PRIVATE HELPERS --------------------------------- */
    private Community findCommunityOrThrow(Long id) {
        return communityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Community not found with id " + id));
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    }

    private User findUserOrNull(Long id) {
        return id == null ? null : userRepository.findById(id).orElse(null);
    }

    private void requireAdmin(CustomUserDetails currentUser) {
        if (!currentUser.getUser().getRole().getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("User is not admin");
        }
    }

    private void requireAdminOrManager(CustomUserDetails currentUser, Community community) {
        boolean isAdmin = currentUser.getUser().getRole().getRole().equals(Role.ROLE_ADMIN);
        boolean isManager = community.getCommunityManager() != null
                && community.getCommunityManager().getId().equals(currentUser.getId());

        if (!isAdmin && !isManager) {
            throw new AccessDeniedException("You cannot perform this action");
        }
    }

}
