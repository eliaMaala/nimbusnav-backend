package com.nimbusnav.flightmanagement.repositories;

import com.nimbusnav.flightmanagement.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    // جلب الإشعارات التي فشلت ولم يتم حلها بعد
    List<Notification> findBySuccessFalseAndRetryCountLessThan(int maxRetries);

    Optional<Notification> findTopByRecipientAndSubjectAndSuccessFalseOrderByCreatedAtAsc(String recipient, String subject);

    @Query("SELECT n FROM Notification n WHERE n.recipient = :recipient AND n.subject = :subject AND n.success = false AND n.resolved = false ORDER BY n.createdAt DESC LIMIT 1")
    List<Notification> findByRecipientAndSubjectAndSuccessFalse(@Param("recipient") String recipient, @Param("subject") String subject);

}

