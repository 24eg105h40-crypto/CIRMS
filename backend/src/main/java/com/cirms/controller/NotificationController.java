package com.cirms.controller;

import com.cirms.dto.ApiResponse;
import com.cirms.dto.NotificationDTO;
import com.cirms.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getUserNotifications(@PathVariable Long userId) {
        List<NotificationDTO> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", notifications));
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@PathVariable Long userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success("Unread count retrieved successfully", count));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable Long id, @RequestParam Long userId) {
        notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", null));
    }

    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked as read", null));
    }
}
