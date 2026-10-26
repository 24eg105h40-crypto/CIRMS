package com.cirms.service;

import com.cirms.dto.NotificationDTO;

import java.util.List;

public interface NotificationService {
    List<NotificationDTO> getUserNotifications(Long userId);
    long getUnreadCount(Long userId);
    void markAsRead(Long notificationId, Long userId);
    void markAllAsRead(Long userId);
    NotificationDTO createNotification(Long userId, String title, String message, String type);
}
