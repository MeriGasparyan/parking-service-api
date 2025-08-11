package com.merigasparyan.jmp.parkingserviceapi.persistance.repository;

import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    @Query("SELECT c FROM Community c JOIN FETCH c.communityManager WHERE c.id = :id")
    Optional<Community> findByIdWithManager(@Param("id") Long id);

    @Query("SELECT c FROM Community c WHERE c.communityManager.id = :managerId")
    List<Community> findByManagerId(@Param("managerId") Long managerId);

    boolean existsByNameAndAddress(String name, String address);
}
