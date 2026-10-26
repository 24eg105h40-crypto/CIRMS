package com.cirms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotBlank(message = "Item name is required")
    @Column(name = "item_name", nullable = false)
    private String itemName;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String color;

    private String brand;

    @NotBlank(message = "Location is required")
    @Column(nullable = false)
    private String location;

    @NotNull(message = "Date is required")
    @Column(name = "date_lost_or_found", nullable = false)
    private LocalDate dateLostOrFound;

    @Column(name = "time_lost_or_found")
    private String timeLostOrFound;

    @NotNull(message = "Item type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ItemType itemType;

    @Column(name = "image_url")
    private String imageUrl;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemStatus status;

    @Column(name = "additional_information", columnDefinition = "TEXT")
    private String additionalInformation;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Claim> claims = new ArrayList<>();

    public Item() {
    }

    public Item(Long id, User user, Category category, String itemName, String description, String color, String brand, String location, LocalDate dateLostOrFound, String timeLostOrFound, ItemType itemType, String imageUrl, ItemStatus status, String additionalInformation, LocalDateTime createdAt, LocalDateTime updatedAt, List<Claim> claims) {
        this.id = id;
        this.user = user;
        this.category = category;
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
        this.claims = claims != null ? claims : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private User user;
        private Category category;
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
        private List<Claim> claims = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder user(User user) { this.user = user; return this; }
        public Builder category(Category category) { this.category = category; return this; }
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
        public Builder claims(List<Claim> claims) { this.claims = claims; return this; }

        public Item build() {
            return new Item(id, user, category, itemName, description, color, brand, location, dateLostOrFound, timeLostOrFound, itemType, imageUrl, status, additionalInformation, createdAt, updatedAt, claims);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null && this.itemType != null) {
            this.status = this.itemType == ItemType.LOST ? ItemStatus.LOST : ItemStatus.FOUND;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

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

    public String getTimeLostOrFound() { return timeLostOrFound; }
    public void setTimeLostOrFound(String timeLostOrFound) { this.timeLostOrFound = timeLostOrFound; }

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

    public List<Claim> getClaims() { return claims; }
    public void setClaims(List<Claim> claims) { this.claims = claims; }
}
