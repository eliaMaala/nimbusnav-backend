package com.nimbusnav.flightmanagement.controllers;

import com.nimbusnav.flightmanagement.models.NotificationLog;
import com.nimbusnav.flightmanagement.repositories.NotificationLogRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications API", description = "Endpoints for managing notifications and logs")
public class NotificationController {

    private final NotificationLogRepository notificationLogRepository;

    public NotificationController(NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
    }

    //  جلب جميع سجلات إعادة المحاولة
    @GetMapping("/retry-log")
    @Operation(summary = "Retrieve all notification retry logs", description = "Fetches all retry logs for sent notifications.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification logs retrieved successfully."),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<NotificationLog>> getRetryLogs() {
        return ResponseEntity.ok(notificationLogRepository.findAll());
    }
}
