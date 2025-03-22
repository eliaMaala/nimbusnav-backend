package com.nimbusnav.flightmanagement.services;

import com.nimbusnav.Enum.BookingStatus;
import com.nimbusnav.Enum.NotificationType;
import com.nimbusnav.flightmanagement.models.Booking;
import com.nimbusnav.flightmanagement.models.Flight;
import com.nimbusnav.flightmanagement.models.UserEntity;
import com.nimbusnav.flightmanagement.repositories.BookingRepository;
import com.nimbusnav.flightmanagement.repositories.FlightRepository;
import com.nimbusnav.flightmanagement.repositories.UserRepository;
import com.nimbusnav.security.JwtUtil;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final NotificationService notificationService;

    @Autowired
    public BookingService(BookingRepository bookingRepository, FlightRepository flightRepository, UserRepository userRepository, JwtUtil jwtUtil, NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.notificationService = notificationService;
    }


    public Booking createBooking(UUID flightId,String token){

        // استخراج بيانات المستخدم من التوكن
        String jwt = token.replace("Bearer ","");
        UUID userId =jwtUtil.extractUserId(jwt);

        // البحث عن المستخدم في قاعدة البيانات
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        // البحث عن الرحلة
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(()->new RuntimeException("Flight Not Found"));

        // التحقق مما إذا كان المستخدم قد حجز هذه الرحلة من قبل
        Optional<Booking> existingBooking = bookingRepository.findByUser(user)
                .stream() //نريد البحث عن حجز معين داخل القائمة وليس التعامل مع القائمة كلها
                .filter(booking -> booking.getFlight().getId().equals(flightId)) //هو دالة تصفية (Filtering Function) تقوم بإجراء فلترة على عناصر Stream
                .findFirst(); // يقوم بإرجاع أول عنصر يطابق الفلترة داخل Optional<Booking>

        if (existingBooking.isPresent()) {
            throw new RuntimeException("You have already booked this flight");

        }

            // إنشاء حجز جديد
        Booking booking = new Booking();
        booking.setFlight(flight);
        booking.setUser(user);
        booking.setStatus(BookingStatus.PENDING);

        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(String token){

        // استخراج بيانات المستخدم من التوكن
        String jwt = token.replace("Bearer ","");
        UUID userId = jwtUtil.extractUserId(jwt);

        // البحث عن المستخدم في قاعدة البيانات
        UserEntity user =userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User Not Found"));

        // البحث عن جميع الحجوزات التي قام بها المستخدم
        return bookingRepository.findByUser(user);
    }

    public void cancelBooking(UUID bookingId,String token){

        // استخراج بيانات المستخدم من التوكن
        String jwt = token.replace("Bearer ","");
        UUID userId = jwtUtil.extractUserId(jwt);

        // البحث عن الحجز المطلوب إلغاؤه
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Booking Not Found"));

        // التحقق مما إذا كان المستخدم هو من قام بالحجز
        if(!booking.getUser().getId().equals(userId)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"You are Not Authorized to cancel this Booking");
        }

        // حذف الحجز من قاعدة البيانات
        bookingRepository.delete(booking);
    }

    public Booking getBookingDetails(UUID bookingId,String token){

        //  جلب تفاصيل الحجز
        String jwt = token.replace("Bearer ","");
        UUID userId = jwtUtil.extractUserId(jwt);
        String userRole = jwtUtil.extractRole(jwt);

        //  البحث عن الحجز في قاعدة البيانات
        Booking booking=bookingRepository.findById(bookingId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking Not Found"));

        //  التحقق من أن المستخدم هو المالك
        if(!booking.getUser().getId().equals(userId) && !userRole.equals("ADMIN")){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to view this booking");
        }
        return booking;
    }

    public Booking confirmBooking(UUID bookingId, String token) throws MessagingException {
        String jwt = token.replace("Bearer ", "");
        UUID userId = jwtUtil.extractUserId(jwt);

        //  البحث عن الحجز في قاعدة البيانات
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking Not Found"));

        //  التحقق مما إذا كان الحجز قد تم تأكيده مسبقًا
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            System.out.println("🚀 Booking already confirmed. No need to send email again.");
            return booking;
        }

        //  التأكد من أن المستخدم هو صاحب الحجز
        if (!booking.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to confirm this booking");
        }

        //  تحديث حالة الحجز إلى CONFIRMED
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setUpdatedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        //  إرسال إشعار تأكيد الحجز للمستخدم
        String recipientEmail = booking.getUser().getEmail();
        String subject = "Booking Confirmation - NimbusNav";
        String content = "Dear " + booking.getUser().getUsername() + ",\n\n" +
                "Your booking for flight " + booking.getFlight().getFlightNumber() +
                " has been confirmed successfully!\n\n" +
                "Thank you for choosing NimbusNav.";

        notificationService.sendNotification(recipientEmail, subject, content, NotificationType.EMAIL);

        return booking;
    }




}
