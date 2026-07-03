package com.pbl.notification.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.pbl.notification.entity.Notification;
import com.pbl.notification.repository.NotificationRepository;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationRepository repository;

    @GetMapping("/user/unread-count")
    public long getUnreadCount(
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        return repository
                .countByUserIdAndIsReadFalse(userId);
    }

    @PatchMapping("/{id}/read")
    public Notification markAsRead(
            @PathVariable Long id,
            HttpServletRequest request) {

        Notification notification = repository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notification tidak ditemukan"));

        Long userId = (Long) request.getAttribute("userId");

        if (!notification.getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN);
        }

        notification.setIsRead(true);

        return repository.save(notification);
    }

    @PatchMapping("/user/read-all")
    public String markAllAsRead(
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");

        List<Notification> notifications = repository.findByUserIdAndIsReadFalse(userId);

        for (Notification notification : notifications) {
            notification.setIsRead(true);
        }

        repository.saveAll(notifications);

        return "Semua notifikasi telah ditandai sebagai dibaca.";
    }

    @GetMapping("/user")
    public Page<Notification> getByUser(
            HttpServletRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = (Long) request.getAttribute("userId");

        Pageable pageable = PageRequest.of(page, size);

        return repository
                .findByUserIdOrderByCreatedAtDesc(
                        userId,
                        pageable);
    }
}