package com.merigasparyan.jmp.parkingserviceapi.security;
import com.merigasparyan.jmp.parkingserviceapi.exception.PermissionDeniedException;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.User;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionChecker{

    private final UserRepository userRepository;

    public void checkPermission(CustomUserDetails user, List<String> permissions) {
       for (String permission : permissions) {
           if(hasPermission(user.getId(), permission))
               return;
       }
       throw new PermissionDeniedException("You do not have permission to access this resource.");
    }

    public boolean hasPermission(Long userId, String permission) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return getPermissionsForUser(user.getId()).stream()
                .anyMatch(rp -> rp.equals(permission));
    }

    public Set<String> getPermissionsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return user.getRole().getRolePermissions().stream()
                .map(rp -> rp.getPermission().getName().name())
                .collect(Collectors.toSet());
    }

}
