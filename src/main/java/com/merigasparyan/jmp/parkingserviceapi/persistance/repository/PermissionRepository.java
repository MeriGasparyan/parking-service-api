package com.merigasparyan.jmp.parkingserviceapi.persistance.repository;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
