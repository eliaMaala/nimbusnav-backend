package com.nimbusnav.flightmanagement.models;

import com.nimbusnav.Enum.BookingStatus;
import com.nimbusnav.Enum.PaymentStatus;
import jakarta.persistence.*;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Builder
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "flight_id",updatable = false)
    private Flight flight;

    @ManyToOne
    @JoinColumn(name = "user_id",updatable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist               //لضبط تواريخ الإنشاء والتحديث تلقائيًا
    protected void onCreate(){
        createdAt=LocalDateTime.now();
        updatedAt=LocalDateTime.now();
    }

    @PreUpdate                //لضبط تواريخ الإنشاء والتحديث تلقائيًا
    protected void onUpdate(){
        updatedAt=LocalDateTime.now();
    }

    @Enumerated(EnumType.STRING)
    //@Column(nullable = false)
    private PaymentStatus paymentStatus =PaymentStatus.PENDING; // افتراضيًا يكون في انتظار الدفع

    @Column(name = "amount_paid")
    private double amountPaid;  // المبلغ المدفوع

    @Column(name = "payment_method")
    private String paymentMethod; // طريقة الدفع (بطاقة، PayPal، نقدًا...)

    @Column(name = "payment_date")
    private LocalDateTime paymentDate; // تاريخ الدفع

    public Booking() {}

    public Booking(UUID id, Flight flight, UserEntity user, BookingStatus status, LocalDateTime createdAt, LocalDateTime updatedAt, PaymentStatus paymentStatus, double amountPaid, String paymentMethod, LocalDateTime paymentDate) {
        this.id = id;
        this.flight = flight;
        this.user = user;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.paymentStatus = paymentStatus;
        this.amountPaid = amountPaid;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
}
