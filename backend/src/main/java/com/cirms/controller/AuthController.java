package com.cirms.controller;

import com.cirms.dto.ApiResponse;
import com.cirms.dto.AuthResponse;
import com.cirms.dto.LoginRequest;
import com.cirms.dto.RegisterRequest;
import com.cirms.dto.UserDTO;
import com.cirms.exception.UnauthorizedException;
import com.cirms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = userService.registerUser(request);
        response.setMessage("User registered successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        response.setMessage("Login successful");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedException("User is not authenticated");
        }

        String email = authentication.getName();
        UserDTO user = userService.getCurrentUser(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<String>> checkStatus() {
        return ResponseEntity.ok(ApiResponse.success("Auth service is running", "OK"));
    }
}
