package com.nimbusnav.flightmanagement.controllers;

import com.nimbusnav.flightmanagement.models.Booking;
import com.nimbusnav.flightmanagement.services.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Booking API", description = "Endpoints for managing flight booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Operation(summary = "Create new Booking",description = "Allows a user to book a flight")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Booking created successfully."),
            @ApiResponse(responseCode = "400", description = "Invalid request."),
            @ApiResponse(responseCode = "403", description = "Unauthorized access."),
            @ApiResponse(responseCode = "409", description = "User has already booked this flight."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })

    public ResponseEntity<Booking> createBooking(@RequestParam UUID flightId, @RequestHeader("Authorization") String token){

        Booking booking=bookingService.createBooking(flightId,token);
        return ResponseEntity.ok(booking);
    }

    @GetMapping
    @Operation(summary = "Get all bookings for the user", description = "Retrieves all flight bookings made by the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's bookings retrieved successfully."),
            @ApiResponse(responseCode = "403", description = "Unauthorized access."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })

    public ResponseEntity<List<Booking>> getUserBookings(@RequestHeader("Authorization") String token){

        List<Booking> bookings=bookingService.getUserBookings(token);
        return ResponseEntity.ok(bookings);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a booking", description = "Allows the authenticated user to cancel a booking they made.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Booking cancelled successfully."),
            @ApiResponse(responseCode = "403", description = "Forbidden - User is not authorized to cancel this booking."),
            @ApiResponse(responseCode = "404", description = "Booking not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error.")
    })
    public ResponseEntity<Void> cancelBooking(@PathVariable UUID id,
                                              @RequestHeader("Authorization")String token){
        bookingService.cancelBooking(id,token);
        return ResponseEntity.noContent().build();
    }

    //  جلب تفاصيل الحجز
    @GetMapping("/{id}")
    @Operation(summary = "Get booking details", description = "Fetches details of a specific booking for the logged-in user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking details retrieved successfully."),
            @ApiResponse(responseCode = "403", description = "Unauthorized access - User is not the owner of the booking"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    public ResponseEntity<Booking> getBookingDetails(@PathVariable UUID id,
                                                     @RequestHeader("Authorization")String token){
        Booking booking = bookingService.getBookingDetails(id,token);
        return ResponseEntity.ok(booking);
    }

    @PatchMapping("/{id}/confirm")
    @Operation(summary = "Confirm a booking", description = "Updates the status of a booking from PENDING to CONFIRMED.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking confirmed successfully."),
            @ApiResponse(responseCode = "403", description = "Unauthorized access."),
            @ApiResponse(responseCode = "404", description = "Booking not found."),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<String> confirmBooking(@PathVariable UUID id , @RequestHeader("Authorization") String token) throws MessagingException {
        bookingService.confirmBooking(id,token);
        return ResponseEntity.ok("Booking Confirmed Successfully");
    }
}
