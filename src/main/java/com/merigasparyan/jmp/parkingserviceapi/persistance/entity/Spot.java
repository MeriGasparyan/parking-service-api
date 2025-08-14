package com.merigasparyan.jmp.parkingserviceapi.persistance.entity;
import com.merigasparyan.jmp.parkingserviceapi.enums.SpotType;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "spots")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Spot {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "spots_id_seq")
    @SequenceGenerator(
            name = "spots_id_seq",
            sequenceName = "spots_id_seq")
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SpotType spotType;
}
