package com.nimbusnav.flightmanagement.services;

import com.nimbusnav.Enum.PaymentStatus;
import com.nimbusnav.dto.PaymentRequest;
import com.nimbusnav.flightmanagement.models.Booking;
import com.nimbusnav.flightmanagement.repositories.BookingRepository;
import com.nimbusnav.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final BookingRepository bookingRepository;
    private final JwtUtil jwtUtil;

    public PaymentService(BookingRepository bookingRepository, JwtUtil jwtUtil) {
        this.bookingRepository = bookingRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public Booking processPayment(PaymentRequest request,String token){

        String jwt = token.replace("Bearer ","");
        UUID userId = jwtUtil.extractUserId(jwt);

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Booking not found"));

        //  التحقق من أن المستخدم هو صاحب الحجز
        if (!booking.getUser().getId().equals(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You are not authorized to make this payment");
        }

        //  التحقق مما إذا كان الحجز قد تم دفعه مسبقًا
        if (!booking.getPaymentStatus().equals(PaymentStatus.PENDING)){
            throw new RuntimeException("This booking has already been paid for");
        }

        //  تحديث معلومات الدفع
        booking.setAmountPaid(request.getAmountPaid());
        booking.setPaymentMethod(request.getPaymentMethod());
        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setPaymentDate(LocalDateTime.now());

        return bookingRepository.save(booking);
    }
}
