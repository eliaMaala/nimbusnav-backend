package com.nimbusnav.flightmanagement.models;

import com.nimbusnav.Enum.FlightStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "flights")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //  تعديل طريقة التوليد الصحيحة
    private UUID id;

    @Column(nullable = false, unique = true)
    private String flightNumber;

    @Column(nullable = false)
    private String departure;

    @Column(nullable = false)
    private String arrival;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FlightStatus status; // Scheduled, Delayed, Cancelled

    @ManyToOne(fetch = FetchType.EAGER) //  تحسين الأداء
    @JoinColumn(name = "createdBy", nullable = false)
    private UserEntity createdBy;

    // 🔹 ربط الرحلة بالمستخدم الذي أنشأها
    @Column(nullable = false)
    private UUID ownerId;

    //  Constructor فارغ مطلوب من Hibernate
    public Flight() {}

    //  Constructor كامل
    public Flight(UUID id, String flightNumber, String departure,
                  LocalDateTime departureTime, String arrival, LocalDateTime arrivalTime,
                  FlightStatus status, UserEntity createdBy,UUID ownerId) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.departure = departure;
        this.departureTime = departureTime;
        this.arrival = arrival;
        this.arrivalTime = arrivalTime;
        this.status = status;
        this.createdBy = createdBy;
        this.ownerId = ownerId;
    }

    //  Getters & Setters
    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public UserEntity getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserEntity createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
}
