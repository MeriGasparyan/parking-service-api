package com.merigasparyan.jmp.parkingserviceapi.config;

import com.merigasparyan.jmp.parkingserviceapi.enums.Permission;
import com.merigasparyan.jmp.parkingserviceapi.enums.Role;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.RolePermission;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.PermissionRepository;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.RolePermissionRepository;
import com.merigasparyan.jmp.parkingserviceapi.persistance.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PermissionConfig {
    private final PermissionRepository permissionRepository;
    private final RoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @PostConstruct
    public void seedPermissions() {

        Map<Permission, com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Permission> permissionMap =
                new EnumMap<>(Permission.class);
        for (Permission permission : Permission.values()) {
            com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Permission permissionEntity = permissionRepository.findByName(permission)
                    .orElseGet(() -> {
                        com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Permission p = new com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Permission();
                        p.setName(permission);
                        return permissionRepository.save(p);
                    });
            permissionMap.put(permission, permissionEntity);
        }
        ;

        Map<Role, Set<Permission>> rolePermissionMapping = Map.of(
                Role.ROLE_RESIDENT, Set.of(
                        Permission.CREATE_ACCOUNT,
                        Permission.CREATE_BOOKING,
                        Permission.VIEW_OWN_BOOKING,
                        Permission.CANCEL_OWN_BOOKING,
                        Permission.PARK_IN_SPOT,
                        Permission.RELEASE_SPOT,
                        Permission.CREATE_GUEST_BOOKING,

                        Permission.VIEW_AVAILABLE_SPOT
                ),

                Role.ROLE_ADMIN, EnumSet.allOf(Permission.class),

                Role.ROLE_COMMUNITY_MANAGER, Set.of(
                        Permission.CREATE_SPOT,
                        Permission.UPDATE_SPOT,
                        Permission.DELETE_SPOT,

                        Permission.VIEW_ALL_BOOKINGS,
                        Permission.CANCEL_BOOKING,

                        Permission.INVITE_RESIDENT,
                        Permission.REMOVE_RESIDENT,
                        Permission.INVITE_GUEST,
                        Permission.REMOVE_GUEST,
                        Permission.VIEW_AVAILABLE_SPOT
                ),

                Role.ROLE_COMMUNITY_GUARDIAN, Set.of(
                        Permission.VIEW_CURRENT_BOOKINGS,
                        Permission.CHANGE_BOOKING_STATUS
                )
        );

        for (var entry : rolePermissionMapping.entrySet()) {
            Role roleName = entry.getKey();
            Set<Permission> assignedPermissions = entry.getValue();

            com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Role role = userRoleRepository.findByRole(roleName)
                    .orElseGet(() -> userRoleRepository.save(new com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Role(roleName)));

            for (Permission permName : assignedPermissions) {
                com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Permission permission = permissionMap.get(permName);

                boolean alreadyExists = rolePermissionRepository.existsByRoleAndPermission(role, permission);
                if (!alreadyExists) {
                    RolePermission rp = new RolePermission();
                    rp.setRole(role);
                    rp.setPermission(permission);
                    rolePermissionRepository.save(rp);
                }
            }
        }
    }
}
