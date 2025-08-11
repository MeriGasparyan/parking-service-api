package com.merigasparyan.jmp.parkingserviceapi.persistance.repository;

import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("""
    SELECT DISTINCT u FROM User u
    LEFT JOIN FETCH u.role r
    LEFT JOIN FETCH r.rolePermissions rp
    LEFT JOIN FETCH rp.permission
    WHERE u.email = :email
""")
    Optional<User> findUserWithRoleAndPermissionsByEmail(@Param("email") String email);


    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.email = :email")
    Optional<User> findByEmailWithRole(@Param("email") String email);

    @Query("SELECT u FROM User u JOIN FETCH u.role r JOIN FETCH r.rolePermissions rp JOIN FETCH rp.permission WHERE u.email = :email")
    Optional<User> findByEmailWithRoleAndPermissions(@Param("email") String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN Community c" +
            " WHERE u.role.role = 'ROLE_COMMUNITY_MANAGER' " +
            "AND c.communityManager.id = :communityId")
    Optional<User> findCommunityManagerByCommunityId(@Param("communityId") Long communityId);
}
