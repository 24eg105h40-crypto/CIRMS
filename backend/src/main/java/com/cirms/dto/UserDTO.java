package com.cirms.dto;

import com.cirms.entity.Role;
import java.time.LocalDateTime;

public class UserDTO {
    private Long id;
    private String fullName;
    private String studentId;
    private String email;
    private String phone;
    private Role role;
    private String profileImage;
    private LocalDateTime createdAt;

    public UserDTO() {}

    public UserDTO(Long id, String fullName, String studentId, String email, String phone, Role role, String profileImage, LocalDateTime createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.studentId = studentId;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.profileImage = profileImage;
        this.createdAt = createdAt;
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
        private Role role;
        private String profileImage;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder studentId(String studentId) { this.studentId = studentId; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder role(Role role) { this.role = role; return this; }
        public Builder profileImage(String profileImage) { this.profileImage = profileImage; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public UserDTO build() {
            return new UserDTO(id, fullName, studentId, email, phone, role, profileImage, createdAt);
        }
    }

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

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
