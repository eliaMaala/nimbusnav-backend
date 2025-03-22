package com.nimbusnav.flightmanagement.services;

import com.nimbusnav.Enum.FlightStatus;
import com.nimbusnav.flightmanagement.models.Flight;
import com.nimbusnav.flightmanagement.models.UserEntity;
import com.nimbusnav.flightmanagement.repositories.FlightRepository;
import com.nimbusnav.flightmanagement.repositories.UserRepository;
import com.nimbusnav.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@Service
public class FlightService {

    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Autowired
    public FlightService(FlightRepository flightRepository, UserRepository userRepository, JwtUtil jwtUtil) {
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    private UUID extractUserIdFromToken(String token) {
        return jwtUtil.extractUserId(token.replace("Bearer ", ""));
    }

    //  إنشاء رحلة جديدة
    @Transactional
    public Flight createFlight(Flight flight, String token) {
        UUID userId = extractUserIdFromToken(token);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        flight.setCreatedBy(user);
        return flightRepository.save(flight);
    }

    //  جلب جميع الرحلات (للمسؤولين فقط)
    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    //  جلب جميع الرحلات التي أنشأها مستخدم معين
    public List<Flight> getUserFlights(String token) {
        UUID userId = extractUserIdFromToken(token);
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        return flightRepository.findByCreatedBy(user);
    }

    //  حذف الرحلة بعد التحقق من الأذونات
    @Transactional
    public void deleteFlight(UUID flightId, String token) throws AccessDeniedException {
        UUID userId = extractUserIdFromToken(token);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight Not Found"));

        //  التحقق مما إذا كان المستخدم هو صاحب الرحلة
        if (!flight.getCreatedBy().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to delete this flight");
        }

        flightRepository.delete(flight);
    }

    //  البحث عن الرحلات حسب الحالة
    public List<Flight> getFlightByStatus(FlightStatus status) {
        return flightRepository.findByStatus(status);
    }
}
