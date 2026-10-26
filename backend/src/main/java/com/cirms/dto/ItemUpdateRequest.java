package com.cirms.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public class ItemUpdateRequest {

    @Size(min = 2, max = 150, message = "Item name must be between 2 and 150 characters")
    private String itemName;

    private Long categoryId;
    private String category;

    @Size(min = 5, max = 3000, message = "Description must be between 5 and 3000 characters")
    private String description;

    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;

    @Size(max = 100, message = "Brand must not exceed 100 characters")
    private String brand;

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

    private String imageUrl;

    @Size(max = 2000, message = "Additional information must not exceed 2000 characters")
    private String additionalInformation;

    private Boolean removeImage;

    @JsonIgnore
    private MultipartFile image;

    public ItemUpdateRequest() {}

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

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getAdditionalInformation() { return additionalInformation; }
    public void setAdditionalInformation(String additionalInformation) { this.additionalInformation = additionalInformation; }

    public Boolean getRemoveImage() { return removeImage; }
    public void setRemoveImage(Boolean removeImage) { this.removeImage = removeImage; }

    public MultipartFile getImage() { return image; }
    public void setImage(MultipartFile image) { this.image = image; }
}
