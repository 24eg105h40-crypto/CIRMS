package com.cirms.dto;

public class AuthResponse {
    private boolean success = true;
    private String message = "Operation successful";
    private String token;
    private String tokenType = "Bearer";
    private UserDTO user;

    public AuthResponse() {}

    public AuthResponse(boolean success, String message, String token, String tokenType, UserDTO user) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.user = user;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean success = true;
        private String message = "Operation successful";
        private String token;
        private String tokenType = "Bearer";
        private UserDTO user;

        public Builder success(boolean success) { this.success = success; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder token(String token) { this.token = token; return this; }
        public Builder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public Builder user(UserDTO user) { this.user = user; return this; }

        public AuthResponse build() {
            return new AuthResponse(success, message, token, tokenType, user);
        }
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }
}
