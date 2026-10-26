package com.cirms.dto;

import com.cirms.entity.ItemType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class ItemCreateRequest {

    @NotBlank(message = "Item name is required")
    @Size(min = 2, max = 150, message = "Item name must be between 2 and 150 characters")
    private String itemName;

    private Long categoryId;
    private String category;

    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 3000, message = "Description must be between 5 and 3000 characters")
    private String description;

    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;

    @Size(max = 100, message = "Brand must not exceed 100 characters")
    private String brand;

    @NotBlank(message = "Location is required")
    @Size(min = 2, max = 200, message = "Location must be between 2 and 200 characters")
    private String location;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateLostOrFound;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateLost;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFound;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    private String timeLostOrFound;
    private String timeLost;
    private String timeFound;
    private String time;

    private ItemType itemType;

    private String imageUrl;

    @Size(max = 2000, message = "Additional information must not exceed 2000 characters")
    private String additionalInformation;

    @JsonIgnore
    private MultipartFile image;

    public ItemCreateRequest() {}

    public ItemCreateRequest(String itemName, Long categoryId, String category, String description, String color, String brand, String location, LocalDate dateLostOrFound, LocalDate dateLost, LocalDate dateFound, LocalDate date, String timeLostOrFound, String timeLost, String timeFound, String time, ItemType itemType, String imageUrl, String additionalInformation, MultipartFile image) {
        this.itemName = itemName;
        this.categoryId = categoryId;
        this.category = category;
        this.description = description;
        this.color = color;
        this.brand = brand;
        this.location = location;
        this.dateLostOrFound = dateLostOrFound;
        this.dateLost = dateLost;
        this.dateFound = dateFound;
        this.date = date;
        this.timeLostOrFound = timeLostOrFound;
        this.timeLost = timeLost;
        this.timeFound = timeFound;
        this.time = time;
        this.itemType = itemType;
        this.imageUrl = imageUrl;
        this.additionalInformation = additionalInformation;
        this.image = image;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String itemName;
        private Long categoryId;
        private String category;
        private String description;
        private String color;
        private String brand;
        private String location;
        private LocalDate dateLostOrFound;
        private LocalDate dateLost;
        private LocalDate dateFound;
        private LocalDate date;
        private String timeLostOrFound;
        private String timeLost;
        private String timeFound;
        private String time;
        private ItemType itemType;
        private String imageUrl;
        private String additionalInformation;
        private MultipartFile image;

        public Builder itemName(String itemName) { this.itemName = itemName; return this; }
        public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder color(String color) { this.color = color; return this; }
        public Builder brand(String brand) { this.brand = brand; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder dateLostOrFound(LocalDate dateLostOrFound) { this.dateLostOrFound = dateLostOrFound; return this; }
        public Builder dateLost(LocalDate dateLost) { this.dateLost = dateLost; return this; }
        public Builder dateFound(LocalDate dateFound) { this.dateFound = dateFound; return this; }
        public Builder date(LocalDate date) { this.date = date; return this; }
        public Builder timeLostOrFound(String timeLostOrFound) { this.timeLostOrFound = timeLostOrFound; return this; }
        public Builder timeLost(String timeLost) { this.timeLost = timeLost; return this; }
        public Builder timeFound(String timeFound) { this.timeFound = timeFound; return this; }
        public Builder time(String time) { this.time = time; return this; }
        public Builder itemType(ItemType itemType) { this.itemType = itemType; return this; }
        public Builder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }
        public Builder additionalInformation(String additionalInformation) { this.additionalInformation = additionalInformation; return this; }
        public Builder image(MultipartFile image) { this.image = image; return this; }

        public ItemCreateRequest build() {
            return new ItemCreateRequest(itemName, categoryId, category, description, color, brand, location,
                    dateLostOrFound, dateLost, dateFound, date, timeLostOrFound, timeLost, timeFound, time,
                    itemType, imageUrl, additionalInformation, image);
        }
    }

    public LocalDate resolveDate() {
        if (dateLostOrFound != null) return dateLostOrFound;
        if (dateLost != null) return dateLost;
        if (dateFound != null) return dateFound;
        if (date != null) return date;
        return null;
    }

    public String resolveTime() {
        if (timeLostOrFound != null && !timeLostOrFound.isBlank()) return timeLostOrFound;
        if (timeLost != null && !timeLost.isBlank()) return timeLost;
        if (timeFound != null && !timeFound.isBlank()) return timeFound;
        if (time != null && !time.isBlank()) return time;
        return null;
    }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

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

    public LocalDate getDateLost() { return dateLost; }
    public void setDateLost(LocalDate dateLost) { this.dateLost = dateLost; }

    public LocalDate getDateFound() { return dateFound; }
    public void setDateFound(LocalDate dateFound) { this.dateFound = dateFound; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getTimeLostOrFound() { return timeLostOrFound; }
    public void setTimeLostOrFound(String timeLostOrFound) { this.timeLostOrFound = timeLostOrFound; }

    public String getTimeLost() { return timeLost; }
    public void setTimeLost(String timeLost) { this.timeLost = timeLost; }

    public String getTimeFound() { return timeFound; }
    public void setTimeFound(String timeFound) { this.timeFound = timeFound; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public ItemType getItemType() { return itemType; }
    public void setItemType(ItemType itemType) { this.itemType = itemType; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAdditionalInformation() { return additionalInformation; }
    public void setAdditionalInformation(String additionalInformation) { this.additionalInformation = additionalInformation; }

    public MultipartFile getImage() { return image; }
    public void setImage(MultipartFile image) { this.image = image; }
}
