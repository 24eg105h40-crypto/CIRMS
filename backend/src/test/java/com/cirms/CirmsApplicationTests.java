package com.cirms;

import com.cirms.dto.AuthResponse;
import com.cirms.dto.ItemCreateRequest;
import com.cirms.dto.ItemDTO;
import com.cirms.dto.RegisterRequest;
import com.cirms.dto.UserDTO;
import com.cirms.entity.*;
import com.cirms.repository.*;
import com.cirms.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CirmsApplicationTests {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CategoryService categoryService;

    @Test
    @DisplayName("Context loads successfully")
    void contextLoads() {
        assertNotNull(categoryRepository);
        assertNotNull(userRepository);
        assertNotNull(itemRepository);
        assertNotNull(claimRepository);
        assertNotNull(notificationRepository);
    }

    @Test
    @DisplayName("Categories are automatically seeded on startup")
    void testDefaultCategoriesSeeded() {
        List<Category> categories = categoryRepository.findAll();
        assertFalse(categories.isEmpty(), "Categories should not be empty");
        assertTrue(categoryRepository.existsByName("Electronics"), "Electronics category should exist");
        assertTrue(categoryRepository.existsByName("ID Card"), "ID Card category should exist");
        assertTrue(categoryRepository.existsByName("Wallet"), "Wallet category should exist");
    }

    @Test
    @Transactional
    @DisplayName("User creation and retrieval works")
    void testUserCreation() {
        String uniqueEmail = "validation_test_" + System.currentTimeMillis() + "@campus.edu";
        RegisterRequest request = RegisterRequest.builder()
                .fullName("Test Student")
                .studentId("ST1001")
                .email(uniqueEmail)
                .phone("9999999999")
                .password("testpass123")
                .build();

        AuthResponse response = userService.registerUser(request);
        UserDTO created = response.getUser();
        assertNotNull(created.getId());
        assertEquals("Test Student", created.getFullName());
        assertEquals(Role.STUDENT, created.getRole());

        Optional<User> found = userRepository.findByEmail(uniqueEmail);
        assertTrue(found.isPresent());
        assertNotEquals("testpass123", found.get().getPassword(), "Password should be hashed");
    }

    @Test
    @Transactional
    @DisplayName("Item creation and search query works")
    void testItemCreationAndSearch() {
        Category category = categoryRepository.findByName("Electronics")
                .orElseGet(() -> categoryRepository.save(Category.builder().name("Electronics").description("Devices").build()));

        User user = userRepository.save(User.builder()
                .fullName("Item Reporter")
                .email("reporter_" + System.currentTimeMillis() + "@campus.edu")
                .password("$2a$10$abcdefghijklmnopqrstuvwxyz1234567890")
                .role(Role.STUDENT)
                .build());

        ItemCreateRequest itemRequest = ItemCreateRequest.builder()
                .categoryId(category.getId())
                .itemName("MacBook Pro 14")
                .description("Silver MacBook Pro with sticker on top")
                .location("Campus Cafe")
                .color("Silver")
                .brand("Apple")
                .dateLostOrFound(LocalDate.now())
                .timeLostOrFound("11:30 AM")
                .itemType(ItemType.LOST)
                .additionalInformation("Left near table 4")
                .build();

        ItemDTO createdItem = itemService.createLostItem(itemRequest, null, user.getId());
        assertNotNull(createdItem.getId());
        assertEquals("MacBook Pro 14", createdItem.getItemName());
        assertEquals(ItemStatus.LOST, createdItem.getStatus());

        // Test search query
        Page<ItemDTO> results = itemService.searchItems(
                ItemType.LOST,
                ItemStatus.LOST,
                category.getId(),
                "MacBook",
                "Cafe",
                null,
                null,
                null,
                null,
                PageRequest.of(0, 10)
        );

        assertNotNull(results);
        assertTrue(results.getContent().stream().anyMatch(i -> i.getId().equals(createdItem.getId())));
    }

    @Test
    @Transactional
    @DisplayName("Notification creation and unread count works")
    void testNotifications() {
        User user = userRepository.save(User.builder()
                .fullName("Notification Target")
                .email("notify_" + System.currentTimeMillis() + "@campus.edu")
                .password("$2a$10$abcdefghijklmnopqrstuvwxyz1234567890")
                .role(Role.STUDENT)
                .build());

        Notification notif = notificationRepository.save(Notification.builder()
                .user(user)
                .title("Welcome to CIRMS")
                .message("Your account is now ready to report and track lost/found items.")
                .type("SYSTEM")
                .isRead(false)
                .build());

        assertNotNull(notif.getId());
        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(user.getId());
        assertEquals(1, unreadCount);

        notificationRepository.markAllAsReadByUserId(user.getId());
        long updatedUnread = notificationRepository.countByUserIdAndIsReadFalse(user.getId());
        assertEquals(0, updatedUnread);
    }
}
