package com.merigasparyan.jmp.parkingserviceapi.persistance.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "permissions")
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
    @Id
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private com.merigasparyan.jmp.parkingserviceapi.enums.Permission name;
}
