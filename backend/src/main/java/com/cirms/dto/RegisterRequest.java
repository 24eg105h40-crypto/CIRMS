package com.cirms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Student ID is required")
    private String studentId;

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String email;

    @NotBlank(message = "Phone number is required")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    public RegisterRequest() {}

    public RegisterRequest(String fullName, String studentId, String email, String phone, String password) {
        this.fullName = fullName;
        this.studentId = studentId;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String fullName;
        private String studentId;
        private String email;
        private String phone;
        private String password;

        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder studentId(String studentId) { this.studentId = studentId; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder password(String password) { this.password = password; return this; }

        public RegisterRequest build() {
            return new RegisterRequest(fullName, studentId, email, phone, password);
        }
    }

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
}
