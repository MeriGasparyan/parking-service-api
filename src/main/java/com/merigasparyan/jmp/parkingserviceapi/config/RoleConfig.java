package com.merigasparyan.jmp.parkingserviceapi.config;

import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Role;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleConfig {
    private final RoleRepository roleRepository;

    @PostConstruct
    public void seedRoles() {
        for (com.merigasparyan.jmp.parkingserviceapi.enums.Role roleEnum : com.merigasparyan.jmp.parkingserviceapi.enums.Role.values()) {
            roleRepository.findByRole(roleEnum).orElseGet(() -> {
                Role newRole = new Role(roleEnum);
                return roleRepository.save(newRole);
            });
        }

        System.out.println("Roles added!");
    }
}
