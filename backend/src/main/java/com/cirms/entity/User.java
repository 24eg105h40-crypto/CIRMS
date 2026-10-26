package com.cirms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "student_id")
    private String studentId;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.STUDENT;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Item> items = new ArrayList<>();

    @OneToMany(mappedBy = "claimant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Claim> claims = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Notification> notifications = new ArrayList<>();

    public User() {
    }

    public User(Long id, String fullName, String studentId, String email, String phone, String password, Role role, String profileImage, LocalDateTime createdAt, LocalDateTime updatedAt, List<Item> items, List<Claim> claims, List<Notification> notifications) {
        this.id = id;
        this.fullName = fullName;
        this.studentId = studentId;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role != null ? role : Role.STUDENT;
        this.profileImage = profileImage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.items = items != null ? items : new ArrayList<>();
        this.claims = claims != null ? claims : new ArrayList<>();
        this.notifications = notifications != null ? notifications : new ArrayList<>();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String fullName;
        private String studentId;
        private String email;
        private String phone;
        private String password;
        private Role role = Role.STUDENT;
        private String profileImage;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<Item> items = new ArrayList<>();
        private List<Claim> claims = new ArrayList<>();
        private List<Notification> notifications = new ArrayList<>();

        public Builder id(Long id) { this.id = id; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder studentId(String studentId) { this.studentId = studentId; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder role(Role role) { this.role = role; return this; }
        public Builder profileImage(String profileImage) { this.profileImage = profileImage; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder items(List<Item> items) { this.items = items; return this; }
        public Builder claims(List<Claim> claims) { this.claims = claims; return this; }
        public Builder notifications(List<Notification> notifications) { this.notifications = notifications; return this; }

        public User build() {
            return new User(id, fullName, studentId, email, phone, password, role, profileImage, createdAt, updatedAt, items, claims, notifications);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.role == null) {
            this.role = Role.STUDENT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public List<Claim> getClaims() { return claims; }
    public void setClaims(List<Claim> claims) { this.claims = claims; }

    public List<Notification> getNotifications() { return notifications; }
    public void setNotifications(List<Notification> notifications) { this.notifications = notifications; }
}
