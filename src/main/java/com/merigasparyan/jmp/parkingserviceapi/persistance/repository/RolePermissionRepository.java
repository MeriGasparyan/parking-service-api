package com.merigasparyan.jmp.parkingserviceapi.persistance.repository;

import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Permission;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Role;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    boolean existsByRoleAndPermission(Role role, Permission permission);
}
