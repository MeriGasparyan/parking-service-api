package com.merigasparyan.jmp.parkingserviceapi.persistance.repository;

import com.merigasparyan.jmp.parkingserviceapi.persistance.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
}
