package com.merigasparyan.jmp.parkingserviceapi.persistance.repository;
import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRole(com.merigasparyan.jmp.parkingserviceapi.enums.Role role);
}
