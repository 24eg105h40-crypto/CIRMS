package com.cirms.service.impl;

import com.cirms.dto.AuthResponse;
import com.cirms.dto.LoginRequest;
import com.cirms.dto.RegisterRequest;
import com.cirms.dto.UserDTO;
import com.cirms.entity.Role;
import com.cirms.entity.User;
import com.cirms.exception.BadRequestException;
import com.cirms.exception.ResourceNotFoundException;
import com.cirms.exception.UnauthorizedException;
import com.cirms.repository.UserRepository;
import com.cirms.security.JwtService;
import com.cirms.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponse registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered: " + request.getEmail());
        }

        if (request.getStudentId() != null && !request.getStudentId().trim().isEmpty()) {
            if (userRepository.existsByStudentId(request.getStudentId())) {
                throw new BadRequestException("Student ID is already registered: " + request.getStudentId());
            }
        }

        User user = User.builder()
                .fullName(request.getFullName().trim())
                .studentId(request.getStudentId() != null ? request.getStudentId().trim() : null)
                .email(request.getEmail().trim().toLowerCase())
                .phone(request.getPhone() != null ? request.getPhone().trim() : null)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STUDENT)
                .build();

        User saved = userRepository.save(user);
        UserDTO userDTO = mapToDTO(saved);
        String token = jwtService.generateToken(saved.getEmail(), saved.getId(), saved.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userDTO)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        UserDTO userDTO = mapToDTO(user);
        String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userDTO)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(String email) {
        User user = getUserEntityByEmail(email);
        return mapToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        return mapToDTO(getUserEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        return mapToDTO(getUserEntityByEmail(email));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByStudentId(String studentId) {
        return userRepository.existsByStudentId(studentId);
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .studentId(user.getStudentId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .profileImage(user.getProfileImage())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
