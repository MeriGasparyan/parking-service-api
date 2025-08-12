package com.merigasparyan.jmp.parkingserviceapi.security;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.User;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PermissionChecker{

    private final UserRepository userRepository;

    public void checkPermission(CustomUserDetails user, String... permissions) {
        if (!hasPermission(user.getId(), permissions)) {
            throw new AccessDeniedException("Permission denied. Required any of: " + String.join(", ", permissions));
        }
    }

    public Set<String> getPermissionsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Set<String> permissions = new HashSet<>();
        user.getRole().getRolePermissions().forEach(rp ->
                permissions.add(rp.getPermission().getName().name()));
        return permissions;
    }

    public boolean hasPermission(Long userId, String... permissions) {
        Set<String> userPermissions = getPermissionsForUser(userId);
        for (String permission : permissions) {
            if (userPermissions.contains(permission)) {
                return true;
            }
        }
        return false;
    }

}
