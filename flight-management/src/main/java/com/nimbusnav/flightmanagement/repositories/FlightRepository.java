package com.nimbusnav.flightmanagement.repositories;

import com.nimbusnav.Enum.FlightStatus;
import com.nimbusnav.flightmanagement.models.Flight;
import com.nimbusnav.flightmanagement.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional(readOnly = true) //  تحسين الأداء في أثناء عمليات القراءة فقط
public interface FlightRepository extends JpaRepository<Flight, UUID> {

    List<Flight> findByCreatedBy(UserEntity user); //  البحث باستخدام الكائن نفسه

    Optional<Flight> findById(UUID id); //  استخدام Optional لتجنب NullPointerException

    List<Flight> findByStatus(FlightStatus status);

    //  البحث عن الرحلات بحالة معينة وللمالك المحدد
    List<Flight> findByStatusAndOwnerId(FlightStatus status, UUID ownerId);
}
