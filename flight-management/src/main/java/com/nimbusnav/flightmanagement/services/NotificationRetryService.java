package com.nimbusnav.flightmanagement.services;

import com.nimbusnav.flightmanagement.models.Notification;
import com.nimbusnav.flightmanagement.models.NotificationLog;
import com.nimbusnav.flightmanagement.repositories.NotificationLogRepository;
import com.nimbusnav.flightmanagement.repositories.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationRetryService {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final NotificationLogRepository notificationLogRepository;

    @PersistenceContext
    private jakarta.persistence.EntityManager entityManager;

    public NotificationRetryService(NotificationService notificationService,
                                    NotificationRepository notificationRepository,
                                    NotificationLogRepository notificationLogRepository) {
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
        this.notificationLogRepository = notificationLogRepository;
    }

    private static final int MAX_RETRIES = 3;

    @Scheduled(fixedRate = 60000) // كل دقيقة
    @Transactional
    public void retryFailedNotification() {
        List<Notification> failedNotifications = notificationRepository.findBySuccessFalseAndRetryCountLessThan(MAX_RETRIES);

        for (Notification notification : failedNotifications) {
            boolean success =notificationService.sendNotification(notification.getRecipient(), notification.getSubject(), notification.getContent(), notification.getType());

            if (!success) {
                notification.setRetryCount(notification.getRetryCount() + 1);
                notificationRepository.save(notification);
            }
        }
    }


}




