package com.nimbusnav.flightmanagement.repositories;

import com.nimbusnav.flightmanagement.models.Booking;
import com.nimbusnav.flightmanagement.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByUser(UserEntity user);
    List<Booking> findByUserId(UUID userId);
}
