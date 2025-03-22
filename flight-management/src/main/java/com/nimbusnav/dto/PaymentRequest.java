package com.nimbusnav.dto;



import java.util.UUID;


public class PaymentRequest {

    private UUID bookingId;       // معرف الحجز الذي سيتم الدفع له
    private double amountPaid;     // المبلغ المدفوع
    private String paymentMethod;  // طريقة الدفع (بطاقة، PayPal، نقدًا...)


    public PaymentRequest() {}

    public PaymentRequest(UUID bookingId, double amountPaid, String paymentMethod) {
        this.bookingId = bookingId;
        this.amountPaid = amountPaid;
        this.paymentMethod = paymentMethod;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
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
}
