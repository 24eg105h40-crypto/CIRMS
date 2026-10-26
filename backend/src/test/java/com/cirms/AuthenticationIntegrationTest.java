package com.cirms;

import com.cirms.dto.LoginRequest;
import com.cirms.dto.RegisterRequest;
import com.cirms.entity.Role;
import com.cirms.entity.User;
import com.cirms.repository.UserRepository;
import com.cirms.security.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Value("${cirms.admin.email:admin@cirms.com}")
    private String adminEmail;

    @Value("${cirms.admin.password:ChangeThisAdminPassword123!}")
    private String adminPassword;

    @Test
    @Transactional
    @DisplayName("1. Successful student registration returns 201, JWT, and UserDTO without password")
    void testSuccessfulRegistration() throws Exception {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        RegisterRequest request = RegisterRequest.builder()
                .fullName("John Doe")
                .studentId("STU_" + uniqueSuffix)
                .email("john_" + uniqueSuffix + "@campus.edu")
                .phone("9876543210")
                .password("Secr3tPassword!")
                .build();

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.id").exists())
                .andExpect(jsonPath("$.user.fullName").value("John Doe"))
                .andExpect(jsonPath("$.user.email").value("john_" + uniqueSuffix + "@campus.edu"))
                .andExpect(jsonPath("$.user.role").value("STUDENT"))
                .andExpect(jsonPath("$.user.password").doesNotExist())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        assertFalse(jsonNode.has("password"), "Response should never contain plain password");
    }

    @Test
    @Transactional
    @DisplayName("2. Registration with duplicate email returns 400 Bad Request")
    void testDuplicateEmailRegistration() throws Exception {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        String email = "dup_email_" + uniqueSuffix + "@campus.edu";

        userRepository.save(User.builder()
                .fullName("Existing User")
                .studentId("EXIST_" + uniqueSuffix)
                .email(email)
                .phone("1111111111")
                .password(passwordEncoder.encode("Password123!"))
                .role(Role.STUDENT)
                .build());

        RegisterRequest request = RegisterRequest.builder()
                .fullName("Another User")
                .studentId("NEW_" + uniqueSuffix)
                .email(email)
                .phone("2222222222")
                .password("Password123!")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Email is already registered")));
    }

    @Test
    @Transactional
    @DisplayName("3. Registration with duplicate studentId returns 400 Bad Request")
    void testDuplicateStudentIdRegistration() throws Exception {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        String studentId = "DUP_STU_" + uniqueSuffix;

        userRepository.save(User.builder()
                .fullName("Original Student")
                .studentId(studentId)
                .email("orig_" + uniqueSuffix + "@campus.edu")
                .phone("1111111111")
                .password(passwordEncoder.encode("Password123!"))
                .role(Role.STUDENT)
                .build());

        RegisterRequest request = RegisterRequest.builder()
                .fullName("Impostor Student")
                .studentId(studentId)
                .email("impostor_" + uniqueSuffix + "@campus.edu")
                .phone("2222222222")
                .password("Password123!")
                .build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Student ID is already registered")));
    }

    @Test
    @Transactional
    @DisplayName("4. Successful login returns 200, JWT, and user details")
    void testSuccessfulLogin() throws Exception {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        String email = "login_test_" + uniqueSuffix + "@campus.edu";
        String rawPassword = "CorrectPassword123!";

        userRepository.save(User.builder()
                .fullName("Login Student")
                .studentId("LOG_" + uniqueSuffix)
                .email(email)
                .phone("9876543210")
                .password(passwordEncoder.encode(rawPassword))
                .role(Role.STUDENT)
                .build());

        LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .password(rawPassword)
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value(email))
                .andExpect(jsonPath("$.user.role").value("STUDENT"))
                .andExpect(jsonPath("$.user.password").doesNotExist());
    }

    @Test
    @Transactional
    @DisplayName("5. Login with invalid password returns 401 Unauthorized")
    void testInvalidPasswordLogin() throws Exception {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        String email = "bad_pass_" + uniqueSuffix + "@campus.edu";

        userRepository.save(User.builder()
                .fullName("Bad Pass Student")
                .studentId("BP_" + uniqueSuffix)
                .email(email)
                .phone("9876543210")
                .password(passwordEncoder.encode("ActualPassword123!"))
                .role(Role.STUDENT)
                .build());

        LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .password("WrongPassword123!")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @Transactional
    @DisplayName("6. JWT validation works for authenticated requests")
    void testJwtAuthentication() throws Exception {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        String email = "jwt_test_" + uniqueSuffix + "@campus.edu";

        User user = userRepository.save(User.builder()
                .fullName("JWT Student")
                .studentId("JWT_" + uniqueSuffix)
                .email(email)
                .phone("9876543210")
                .password(passwordEncoder.encode("Pass123!"))
                .role(Role.STUDENT)
                .build());

        String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole().name());

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    @Transactional
    @DisplayName("7. GET /api/auth/me returns current user profile without password")
    void testGetCurrentUser() throws Exception {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        String email = "me_test_" + uniqueSuffix + "@campus.edu";

        User user = userRepository.save(User.builder()
                .fullName("Current User Test")
                .studentId("ME_" + uniqueSuffix)
                .email(email)
                .phone("9876543210")
                .password(passwordEncoder.encode("Pass123!"))
                .role(Role.STUDENT)
                .build());

        String token = jwtService.generateToken(user.getEmail(), user.getId(), user.getRole().name());

        MvcResult result = mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Current User Test"))
                .andExpect(jsonPath("$.studentId").value("ME_" + uniqueSuffix))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.phone").value("9876543210"))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        assertFalse(jsonNode.has("password"));
    }

    @Test
    @Transactional
    @DisplayName("8. Student accessing protected API (/api/auth/me) succeeds")
    void testStudentAccessingProtectedApi() throws Exception {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        String email = "student_protected_" + uniqueSuffix + "@campus.edu";

        User student = userRepository.save(User.builder()
                .fullName("Protected Student")
                .studentId("PROT_" + uniqueSuffix)
                .email(email)
                .password(passwordEncoder.encode("Pass123!"))
                .role(Role.STUDENT)
                .build());

        String token = jwtService.generateToken(student.getEmail(), student.getId(), student.getRole().name());

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @DisplayName("9. Student attempting admin API returns 403 Forbidden")
    void testStudentAccessingAdminApiForbidden() throws Exception {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        String email = "student_admin_probe_" + uniqueSuffix + "@campus.edu";

        User student = userRepository.save(User.builder()
                .fullName("Regular Student")
                .studentId("REG_" + uniqueSuffix)
                .email(email)
                .password(passwordEncoder.encode("Pass123!"))
                .role(Role.STUDENT)
                .build());

        String studentToken = jwtService.generateToken(student.getEmail(), student.getId(), student.getRole().name());

        mockMvc.perform(get("/api/admin/ping")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"));
    }

    @Test
    @Transactional
    @DisplayName("10. Admin accessing admin API returns 200 OK")
    void testAdminAccessingAdminApi() throws Exception {
        // Find seeded admin or create admin
        User admin = userRepository.findByEmail(adminEmail)
                .orElseGet(() -> userRepository.save(User.builder()
                        .fullName("Admin User")
                        .studentId("ADMIN001")
                        .email(adminEmail)
                        .password(passwordEncoder.encode(adminPassword))
                        .role(Role.ADMIN)
                        .build()));

        String adminToken = jwtService.generateToken(admin.getEmail(), admin.getId(), admin.getRole().name());

        mockMvc.perform(get("/api/admin/ping")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("PONG"));
    }

    @Test
    @Transactional
    @DisplayName("11. Password is BCrypt hashed and never plain text in database")
    void testPasswordIsBcryptHashed() {
        String plainPassword = "MySuperSecretPlainPassword!";
        RegisterRequest request = RegisterRequest.builder()
                .fullName("Hash Test Student")
                .studentId("HASH_" + System.currentTimeMillis())
                .email("hash_" + System.currentTimeMillis() + "@campus.edu")
                .phone("9876543210")
                .password(plainPassword)
                .build();

        com.cirms.dto.AuthResponse response = mockMvcAndRegister(request);
        User user = userRepository.findById(response.getUser().getId()).orElseThrow();

        assertNotEquals(plainPassword, user.getPassword());
        assertTrue(user.getPassword().startsWith("$2a$") || user.getPassword().startsWith("$2b$"),
                "Password hash should start with BCrypt prefix");
        assertTrue(passwordEncoder.matches(plainPassword, user.getPassword()),
                "BCrypt password encoder should match original plain password");
    }

    @Test
    @Transactional
    @DisplayName("12. Password is never returned in API responses")
    void testPasswordNeverReturnedInResponses() throws Exception {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        String email = "no_pass_" + uniqueSuffix + "@campus.edu";
        String password = "PlainPassword123!";

        RegisterRequest request = RegisterRequest.builder()
                .fullName("No Pass User")
                .studentId("NP_" + uniqueSuffix)
                .email(email)
                .phone("9876543210")
                .password(password)
                .build();

        // 1. Check Register response
        MvcResult regResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String regContent = regResult.getResponse().getContentAsString();
        assertFalse(regContent.contains(password), "Register response must not contain plain password");
        assertFalse(regContent.contains("$2a$"), "Register response must not contain password hash");

        // 2. Check Login response
        LoginRequest loginRequest = LoginRequest.builder()
                .email(email)
                .password(password)
                .build();

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String loginContent = loginResult.getResponse().getContentAsString();
        assertFalse(loginContent.contains(password), "Login response must not contain plain password");
        assertFalse(loginContent.contains("$2a$"), "Login response must not contain password hash");

        JsonNode loginJson = objectMapper.readTree(loginContent);
        String token = loginJson.get("token").asText();

        // 3. Check /me response
        MvcResult meResult = mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String meContent = meResult.getResponse().getContentAsString();
        assertFalse(meContent.contains(password), "Me response must not contain plain password");
        assertFalse(meContent.contains("$2a$"), "Me response must not contain password hash");
    }

    private com.cirms.dto.AuthResponse mockMvcAndRegister(RegisterRequest request) {
        try {
            MvcResult result = mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andReturn();
            return objectMapper.readValue(result.getResponse().getContentAsString(), com.cirms.dto.AuthResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
