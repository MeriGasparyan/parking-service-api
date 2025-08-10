package com.merigasparyan.jmp.parkingserviceapi.security;

import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.User;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userRepository.findUserWithRoleAndPermissionsByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        return new CustomUserDetails(user);
    }
}
