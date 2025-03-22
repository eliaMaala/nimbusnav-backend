package com.nimbusnav.flightmanagement.models;

import com.nimbusnav.Enum.NotificationType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String recipient;  // البريد الإلكتروني أو معرف المستخدم للإشعار الداخلي

    @Column(nullable = false)
    private String subject;   // عنوان الإشعار

    @Column(nullable = false,length = 5000)
    private String content;   // نص الإشعار

    @Enumerated(EnumType.STRING)
    private NotificationType type;  // نوع الإشعار (EMAIL, SMS, IN_APP)

    private boolean success;  // هل تم إرسال الإشعار بنجاح؟

    private int retryCount = 0;  // عدد المحاولات الفاشلة

    private boolean resolved = false; // هل تم التعامل مع الفشل؟

    private LocalDateTime createdAt = LocalDateTime.now();  // وقت إنشاء الإشعار

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<NotificationLog> logs;

    public Notification() {}

    public Notification(UUID id, String recipient, String subject, String content, NotificationType type, boolean success, int retryCount, boolean resolved, LocalDateTime createdAt) {
        this.id = id;
        this.recipient = recipient;
        this.subject = subject;
        this.content = content;
        this.type = type;
        this.success = success;
        this.retryCount = retryCount;
        this.resolved = resolved;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<NotificationLog> getLogs() {
        return logs;
    }

    public void setLogs(List<NotificationLog> logs) {
        this.logs = logs;
    }
}
