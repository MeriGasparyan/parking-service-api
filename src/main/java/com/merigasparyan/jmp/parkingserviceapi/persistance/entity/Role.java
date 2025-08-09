package com.merigasparyan.jmp.parkingserviceapi.persistance.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
@Setter
@Getter
@NoArgsConstructor
public class Role {
    @Id
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    private com.merigasparyan.jmp.parkingserviceapi.enums.Role role;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RolePermission> rolePermissions = new ArrayList<>();

    public void addRolePermission(RolePermission rolePermission) {
        rolePermissions.add(rolePermission);
        rolePermission.setRole(this);
    }

    public void removeRolePermission(RolePermission rolePermission) {
        rolePermissions.remove(rolePermission);
        rolePermission.setRole(null);
    }

    public Role(com.merigasparyan.jmp.parkingserviceapi.enums.Role role) {
        this.role = role;
    }
}
