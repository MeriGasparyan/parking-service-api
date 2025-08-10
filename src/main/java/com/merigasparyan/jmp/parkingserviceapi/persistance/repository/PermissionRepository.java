package com.merigasparyan.jmp.parkingserviceapi.persistance.repository;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(com.merigasparyan.jmp.parkingserviceapi.enums.Permission name);
}
