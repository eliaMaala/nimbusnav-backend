package com.nimbusnav.flightmanagement.controllers;

import com.nimbusnav.dto.PaymentRequest;
import com.nimbusnav.flightmanagement.models.Booking;
import com.nimbusnav.flightmanagement.services.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
@Tag(name = "Payment API", description = "Endpoints for processing payments")
public class PaymentController {

    private final PaymentService paymentService;



    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @Operation(summary = "Process payment", description = "Allows a user to pay for a booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment processed successfully."),
            @ApiResponse(responseCode = "403", description = "Unauthorized access - User is not allowed to pay for this booking"),
            @ApiResponse(responseCode = "404", description = "Booking not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    public ResponseEntity<Booking> processPayment(@RequestBody PaymentRequest request,
                                                  @RequestHeader("Authorization")String token){


        Booking UpdateBooking = paymentService.processPayment(request,token);

        return ResponseEntity.ok(UpdateBooking);
    }
}
