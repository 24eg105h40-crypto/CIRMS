package com.cirms.dto;

import com.cirms.entity.ItemStatus;
import com.cirms.entity.ItemType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ItemDTO {
    private Long id;
    private Long userId;
    private String reporterName;
    private Long categoryId;
    private String categoryName;
    private String itemName;
    private String description;
    private String color;
    private String brand;
    private String location;
    private LocalDate dateLostOrFound;
    private String timeLostOrFound;
    private ItemType itemType;
    private String imageUrl;
    private ItemStatus status;
    private String additionalInformation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ItemDTO() {}

    public ItemDTO(Long id, Long userId, String reporterName, Long categoryId, String categoryName, String itemName, String description, String color, String brand, String location, LocalDate dateLostOrFound, String timeLostOrFound, ItemType itemType, String imageUrl, ItemStatus status, String additionalInformation, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.reporterName = reporterName;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.itemName = itemName;
        this.description = description;
        this.color = color;
        this.brand = brand;
        this.location = location;
        this.dateLostOrFound = dateLostOrFound;
        this.timeLostOrFound = timeLostOrFound;
        this.itemType = itemType;
        this.imageUrl = imageUrl;
        this.status = status;
        this.additionalInformation = additionalInformation;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private String reporterName;
        private Long categoryId;
        private String categoryName;
        private String itemName;
        private String description;
        private String color;
        private String brand;
        private String location;
        private LocalDate dateLostOrFound;
        private String timeLostOrFound;
        private ItemType itemType;
        private String imageUrl;
        private ItemStatus status;
        private String additionalInformation;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder reporterName(String reporterName) { this.reporterName = reporterName; return this; }
        public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public Builder categoryName(String categoryName) { this.categoryName = categoryName; return this; }
        public Builder itemName(String itemName) { this.itemName = itemName; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder color(String color) { this.color = color; return this; }
        public Builder brand(String brand) { this.brand = brand; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder dateLostOrFound(LocalDate dateLostOrFound) { this.dateLostOrFound = dateLostOrFound; return this; }
        public Builder timeLostOrFound(String timeLostOrFound) { this.timeLostOrFound = timeLostOrFound; return this; }
        public Builder itemType(ItemType itemType) { this.itemType = itemType; return this; }
        public Builder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public Builder status(ItemStatus status) { this.status = status; return this; }
        public Builder additionalInformation(String additionalInformation) { this.additionalInformation = additionalInformation; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ItemDTO build() {
            return new ItemDTO(id, userId, reporterName, categoryId, categoryName, itemName, description, color, brand, location, dateLostOrFound, timeLostOrFound, itemType, imageUrl, status, additionalInformation, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDate getDateLostOrFound() { return dateLostOrFound; }
    public void setDateLostOrFound(LocalDate dateLostOrFound) { this.dateLostOrFound = dateLostOrFound; }

    public LocalDate getDate() { return dateLostOrFound; }
    public LocalDate getDateLost() { return dateLostOrFound; }
    public LocalDate getDateFound() { return dateLostOrFound; }

    public String getTimeLostOrFound() { return timeLostOrFound; }
    public void setTimeLostOrFound(String timeLostOrFound) { this.timeLostOrFound = timeLostOrFound; }

    public String getTime() { return timeLostOrFound; }
    public String getTimeLost() { return timeLostOrFound; }
    public String getTimeFound() { return timeLostOrFound; }

    public ItemType getItemType() { return itemType; }
    public void setItemType(ItemType itemType) { this.itemType = itemType; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public ItemStatus getStatus() { return status; }
    public void setStatus(ItemStatus status) { this.status = status; }

    public String getAdditionalInformation() { return additionalInformation; }
    public void setAdditionalInformation(String additionalInformation) { this.additionalInformation = additionalInformation; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
