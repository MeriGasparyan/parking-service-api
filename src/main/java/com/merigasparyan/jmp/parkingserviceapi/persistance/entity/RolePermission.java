package com.merigasparyan.jmp.parkingserviceapi.persistance.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "role_permissions")
@Setter
@Getter
@NoArgsConstructor
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_permission_id_seq")
    @SequenceGenerator(
            name = "role_permission_id_seq",
            sequenceName = "role_permission_id_seq",
            allocationSize = 50)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id")
    private Permission permission;
}

