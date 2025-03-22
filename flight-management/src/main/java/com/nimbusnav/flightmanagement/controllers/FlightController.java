package com.nimbusnav.flightmanagement.controllers;

import com.nimbusnav.Enum.FlightStatus;
import com.nimbusnav.flightmanagement.models.Flight;
import com.nimbusnav.flightmanagement.models.UserEntity;
import com.nimbusnav.flightmanagement.repositories.FlightRepository;
import com.nimbusnav.flightmanagement.repositories.UserRepository;
import com.nimbusnav.flightmanagement.services.FlightService;
import com.nimbusnav.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/flights")
@Tag(name = "Flights API", description = "Endpoints for Flight Management")
public class FlightController {

    private final FlightService flightService;
    private final FlightRepository flightRepository;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public FlightController(FlightService flightService, FlightRepository flightRepository, JwtUtil jwtUtil, UserRepository userRepository) {
        this.flightService = flightService;
        this.flightRepository = flightRepository;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    //  جلب الرحلات التي أنشأها المستخدم
    @GetMapping
    @Operation(summary = "Retrieve flights for the logged-in user", description = "Fetches all flights created by the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's flights retrieved successfully."),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Flight>> getUserFlights(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(flightService.getUserFlights(token.replace("Bearer ", "")));
    }

    //  جلب جميع الرحلات (خاص بالأدمين)
    @GetMapping("/all")
    @Operation(summary = "Retrieve all flights (Admin Only)", description = "Fetches all flights stored in the database. Only accessible by ADMIN users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flights retrieved successfully."),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have admin rights"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Flight>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    //  إنشاء رحلة جديدة (خاص بالأدمين)
    @PostMapping
    @Operation(summary = "Create a new flight", description = "Creates a new flight and adds it to the database. Only accessible by ADMIN users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Flight created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request data."),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have admin rights"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Flight> createFlight(
            @Parameter(description = "Flight details") @RequestBody Flight flight,
            @RequestHeader("Authorization") String token) {
        //  استخراج بيانات المستخدم من التوكن
        String jwt = token.replace("Bearer ", "");
        UUID userId = jwtUtil.extractUserId(jwt);

        //  استرجاع كائن المستخدم من قاعدة البيانات
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));


        // تعيين المستخدم الذي أنشأ الرحلة
        flight.setCreatedBy(user);

        // إذا لم يتم تحديد `ownerId` في الطلب، اجعل `ownerId` هو نفس `createdBy`
        if (flight.getOwnerId() == null) {
            flight.setOwnerId(userId);
        }
        Flight savedFlight = flightRepository.save(flight);

        //  إرجاع بيانات الرحلة التي تم إنشاؤها
        return ResponseEntity.ok(savedFlight);
    }

    //  حذف رحلة باستخدام ID
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a flight", description = "Deletes a specific flight using its ID. Only the creator or an admin can delete it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flight deleted successfully."),
            @ApiResponse(responseCode = "403", description = "Forbidden - User does not have permission to delete this flight."),
            @ApiResponse(responseCode = "404", description = "Flight not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFlight(
            @Parameter(description = "Flight ID") @PathVariable UUID id,
            @RequestHeader("Authorization") String token) throws AccessDeniedException {
        flightService.deleteFlight(id, token.replace("Bearer ", ""));
        return ResponseEntity.ok("Flight deleted successfully");
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateFlightStatus(
            @PathVariable UUID id,
            @RequestParam FlightStatus status,
            @RequestHeader("Authorization") String token) {

        //  استخراج بيانات المستخدم من الـ JWT
        String jwt = token.replace("Bearer ", "");
        String username = jwtUtil.extractUsername(jwt);
        UUID userId = jwtUtil.extractUserId(jwt);

        //  البحث عن الرحلة
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flight Not Found"));

        //  التحقق من ملكية الرحلة
        if (!flight.getOwnerId().equals(userId) && !jwtUtil.extractRole(jwt).equals("ADMIN")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not allowed to update this flight status.");
        }

        //  تحديث الحالة وحفظها
        flight.setStatus(status);
        flightRepository.save(flight);

        return ResponseEntity.ok("Flight status updated to: " + status);
    }


    @GetMapping("/status")
    public ResponseEntity<List<Flight>> getFlightByStatus(@RequestParam FlightStatus status){
        return ResponseEntity.ok(flightService.getFlightByStatus(status));
    }
}
