package com.cirms.dto;

import java.time.LocalDateTime;

public class NotificationDTO {
    private Long id;
    private Long userId;
    private String title;
    private String message;
    private String type;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationDTO() {}

    public NotificationDTO(Long id, Long userId, String title, String message, String type, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private String title;
        private String message;
        private String type;
        private boolean isRead = false;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder isRead(boolean isRead) { this.isRead = isRead; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public NotificationDTO build() {
            return new NotificationDTO(id, userId, title, message, type, isRead, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
