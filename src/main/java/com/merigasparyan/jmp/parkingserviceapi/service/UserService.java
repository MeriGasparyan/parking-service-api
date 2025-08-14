package com.merigasparyan.jmp.parkingserviceapi.service;

import com.merigasparyan.jmp.parkingserviceapi.dto.CreateUserDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.UpdateUserDTO;
import com.merigasparyan.jmp.parkingserviceapi.dto.UserDTO;
import com.merigasparyan.jmp.parkingserviceapi.enums.Permission;
import com.merigasparyan.jmp.parkingserviceapi.enums.Role;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Community;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.User;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.CommunityRepository;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.RoleRepository;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.UserRepository;
import com.merigasparyan.jmp.parkingserviceapi.security.PermissionChecker;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CommunityRepository communityRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionChecker permissionChecker;

    /* ---------------------- CREATE ---------------------- */
    @Transactional
    public UserDTO createUser(CreateUserDTO dto, Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new EntityNotFoundException("Community with id " + communityId + " not found"));
        User user = mapDtoToUser(dto, community, Role.ROLE_RESIDENT);
        return UserDTO.mapToDTO(userRepository.save(user));
    }

    @Transactional
    public UserDTO createAdmin(CreateUserDTO dto) {
        User user = mapDtoToUser(dto, null, Role.ROLE_ADMIN);
        return UserDTO.mapToDTO(userRepository.save(user));
    }

    private User mapDtoToUser(CreateUserDTO dto, Community community, Role roleEnum) {
        User user = new User();
        user.setFirstname(dto.getFirstName());
        user.setLastname(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setCommunity(community);
        user.setRole(roleRepository.findByRole(roleEnum)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleEnum)));
        return user;
    }

    /* ---------------------- UPDATE ---------------------- */
    @Transactional
    public UserDTO updateUser(Long id, Long currentUserId, UpdateUserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));

        boolean isSelf = currentUserId.equals(id);
        boolean hasManagePermission = permissionChecker.hasPermission(currentUserId, Permission.MANAGE_USERS.name());

        if (!isSelf && !hasManagePermission) {
            throw new AccessDeniedException("You can only update your own profile or need MANAGE_USERS permission.");
        }

        if (dto.getFirstname() != null) user.setFirstname(dto.getFirstname());
        if (dto.getLastname() != null) user.setLastname(dto.getLastname());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());

        updatePassword(user, dto);
        updateRole(user, dto, hasManagePermission);

        return UserDTO.mapToDTO(userRepository.save(user));
    }

    private void updatePassword(User user, UpdateUserDTO dto) {
        if (dto.getNewPassword() != null) {
            if (dto.getCurrentPassword() == null ||
                    !passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("Current password is required and must be correct.");
            }
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }
    }

    private void updateRole(User user, UpdateUserDTO dto, boolean hasManagePermission) {
        if (dto.getRole() != null) {
            if (!hasManagePermission) throw new AccessDeniedException("You cannot update your role.");
            var role = roleRepository.findByRole(Role.valueOf(dto.getRole()))
                    .orElseThrow(() -> new EntityNotFoundException("Role not found: " + dto.getRole()));
            user.setRole(role);
        }
    }

    /* ---------------------- READ ---------------------- */
    @Transactional(readOnly = true)
    public UserDTO getUser(Long id) {
        return userRepository.findById(id)
                .map(UserDTO::mapToDTO)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::mapToDTO)
                .collect(Collectors.toList());
    }

    /* ---------------------- DELETE ---------------------- */
    @Transactional
    public void deleteUser(Long id, Long currentUserId) {
        boolean isSelf = currentUserId.equals(id);
        boolean hasManagePermission = permissionChecker.hasPermission(currentUserId, Permission.MANAGE_USERS.name());

        if (!isSelf && !hasManagePermission) {
            throw new AccessDeniedException("You can only delete your own profile or need MANAGE_USERS permission.");
        }

        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id " + id);
        }

        userRepository.deleteById(id);
    }
}
