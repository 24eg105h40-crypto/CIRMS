package com.cirms.service;

import com.cirms.dto.AuthResponse;
import com.cirms.dto.LoginRequest;
import com.cirms.dto.RegisterRequest;
import com.cirms.dto.UserDTO;
import com.cirms.entity.User;

import java.util.List;

public interface UserService {
    AuthResponse registerUser(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserDTO getCurrentUser(String email);
    UserDTO getUserById(Long id);
    UserDTO getUserByEmail(String email);
    User getUserEntityById(Long id);
    User getUserEntityByEmail(String email);
    List<UserDTO> getAllUsers();
    boolean existsByEmail(String email);
    boolean existsByStudentId(String studentId);
}
