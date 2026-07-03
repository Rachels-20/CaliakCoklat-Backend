package com.pbl.notification.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long deviceId;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String severity; // INFO, WARNING, CRITICAL

    private Boolean isRead = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Getter dan Setter
}