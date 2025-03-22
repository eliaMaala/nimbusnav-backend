package com.nimbusnav.flightmanagement.models;

import jakarta.persistence.*;
import org.hibernate.annotations.Fetch;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_log")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;  //  إضافة معرف للكيان


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id",nullable = false)
    private Notification notification;  //  مرجع إلى الإشعار الأصلي

    @Column(nullable = false)
    private LocalDateTime attemptTime;  //  وقت إعادة المحاولة

    @Column(nullable = false)
    private boolean success;  //  هل نجحت المحاولة؟

    @Column(nullable = false)
    private int retryCount;  //  عدد المحاولات قبل هذه المحاولة

    @Column(length = 500)
    private String errorMessage; //  في حالة الفشل، يتم تخزين سبب الفشل


    @Version  //  يضيف آلية القفل المتفائل
    @Column(nullable = false)
    private Integer version= 0 ;

    public NotificationLog() {
        this.version = 0;
    }

    public NotificationLog(UUID id, Notification notification, LocalDateTime attemptTime, boolean success, int retryCount, String errorMessage) {
        this.id = id;
        this.notification = notification;
        this.attemptTime = attemptTime;
        this.success = success;
        this.retryCount = retryCount;
        this.errorMessage = errorMessage;
        this.version = 0;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public LocalDateTime getAttemptTime() {
        return attemptTime;
    }

    public void setAttemptTime(LocalDateTime attemptTime) {
        this.attemptTime = attemptTime;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
