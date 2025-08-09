package com.merigasparyan.jmp.parkingserviceapi.persistance.entity;

import com.merigasparyan.jmp.parkingserviceapi.enums.OccupancyStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "occupancies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Occupancy {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "occupancies_id_seq")
    @SequenceGenerator(
            name = "occupancies_id_seq",
            sequenceName = "occupancies_id_seq",
            allocationSize = 50
    )
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id", nullable = false)
    private Spot spot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OccupancyStatus status;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
}
