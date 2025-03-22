package com.nimbusnav.flightmanagement.services;

import com.nimbusnav.Enum.NotificationType;
import com.nimbusnav.flightmanagement.models.Notification;
import com.nimbusnav.flightmanagement.repositories.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    public NotificationService(JavaMailSender mailSender, NotificationRepository notificationRepository) {
        this.mailSender = mailSender;
        this.notificationRepository = notificationRepository;
    }


    @Transactional
    public boolean sendNotification(String recipient, String subject, String content, NotificationType type) {
        List<Notification> failedNotifications = notificationRepository.findByRecipientAndSubjectAndSuccessFalse(recipient, subject);
        Notification notification;

        if (!failedNotifications.isEmpty()) {
            notification = failedNotifications.get(0);  // أخذ أحدث إشعار فشل
        } else {
            // إنشاء إشعار جديد
            notification = new Notification();
            notification.setRecipient(recipient);
            notification.setSubject(subject);
            notification.setContent(content);
            notification.setType(type);
            notification.setSuccess(false);
            notification.setRetryCount(0);
            notification.setResolved(false);
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }

        boolean success = false;
        try {
            switch (type) {
                case EMAIL:
                    success = sendEmailNotification(notification);
                    break;
                case SMS:
                    sendSMSNotification(notification);
                    success = true;
                    break;
                case IN_APP:
                    sendInAppNotification(notification);
                    success = true;
                    break;
            }

            if (success) {
                notification.setSuccess(true);
                notification.setResolved(true);
                notification.setRetryCount(0);
                notificationRepository.saveAndFlush(notification);  // 🔥 تحديث فوري في قاعدة البيانات
                System.out.println(" Notification sent successfully and updated in DB: " + notification.getId());
            } else {
                notification.setRetryCount(notification.getRetryCount() + 1);
                notificationRepository.saveAndFlush(notification);
                System.err.println(" Failed to send notification: " + notification.getId() + " | Attempt: " + notification.getRetryCount());
            }
        } catch (Exception e) {
            notification.setRetryCount(notification.getRetryCount() + 1);
            notificationRepository.saveAndFlush(notification);
            System.err.println(" Exception while sending notification: " + notification.getId() + " | Error: " + e.getMessage());
        }

        return success;
    }



    private boolean sendEmailNotification(Notification notification) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(notification.getRecipient());
            helper.setSubject(notification.getSubject());
            helper.setText(notification.getContent(), true);
            helper.setFrom("nimbusnavcompany@gmail.com"); //  تأكد من تعيين المرسل بشكل صحيح

            mailSender.send(message);

            System.out.println("📧 Email sent successfully to: " + notification.getRecipient());
            return true;
        } catch (MessagingException e) {
            System.err.println(" Failed to send email to: " + notification.getRecipient() + " | Error: " + e.getMessage());
            return false;
        }
    }


    //  إرسال رسالة نصية (محاكاة)
    private void sendSMSNotification(Notification notification) {
        // هنا يجب دمج خدمة SMS مثل Twilio أو أي API خارجي
        System.out.println("📩 Sending SMS to " + notification.getRecipient());
    }

    //  إرسال إشعار داخل التطبيق (محاكاة)
    private void sendInAppNotification(Notification notification) {
        // هنا يمكن إرسال إشعار دفع عبر Firebase Cloud Messaging أو أي نظام آخر
        System.out.println("🔔 Sending Push Notification to " + notification.getRecipient());
    }
}
