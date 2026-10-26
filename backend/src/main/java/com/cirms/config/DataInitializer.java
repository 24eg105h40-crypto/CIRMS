package com.cirms.config;

import com.cirms.entity.*;
import com.cirms.repository.CategoryRepository;
import com.cirms.repository.ItemRepository;
import com.cirms.repository.UserRepository;
import com.cirms.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

/**
 * DataInitializer — runs once on application startup.
 *
 * Responsibilities:
 *  1. Seeds the 11 default campus categories.
 *  2. Creates the development admin account.
 *  3. Creates 2 demo student accounts (only if the item table is empty).
 *  4. Seeds 10 realistic campus lost/found items designed to demonstrate
 *     the smart matching algorithm (5 intentional high-score pairs).
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final CategoryService      categoryService;
    private final CategoryRepository   categoryRepository;
    private final UserRepository       userRepository;
    private final ItemRepository       itemRepository;
    private final PasswordEncoder      passwordEncoder;

    @Value("${cirms.admin.email:admin@cirms.com}")
    private String adminEmail;

    @Value("${cirms.admin.password:ChangeThisAdminPassword123!}")
    private String adminPassword;

    @Value("${cirms.admin.name:System Administrator}")
    private String adminName;

    public DataInitializer(CategoryService categoryService,
                           CategoryRepository categoryRepository,
                           UserRepository userRepository,
                           ItemRepository itemRepository,
                           PasswordEncoder passwordEncoder) {
        this.categoryService    = categoryService;
        this.categoryRepository = categoryRepository;
        this.userRepository     = userRepository;
        this.itemRepository     = itemRepository;
        this.passwordEncoder    = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        log.info("=== CIRMS DataInitializer starting ===");

        // Step 1: Categories
        log.info("Seeding default campus categories...");
        categoryService.initDefaultCategories();
        log.info("Category initialization complete.");

        // Step 2: Admin account
        initAdminUser();

        // Step 3: Demo students + seed items (only if no items exist yet)
        if (itemRepository.count() == 0) {
            log.info("Item table is empty — seeding demo students and sample items...");
            User student1 = initDemoStudent("Aryan Sharma",     "CS2021001", "aryan@student.campus.edu",   "9876543210");
            User student2 = initDemoStudent("Priya Mehta",      "EE2021002", "priya@student.campus.edu",   "9876543211");
            User student3 = initDemoStudent("Rahul Verma",      "ME2022003", "rahul@student.campus.edu",   "9876543212");
            seedItems(student1, student2, student3);
            log.info("Demo seed data created successfully.");
        } else {
            log.info("Item table already has data — skipping seed items.");
        }

        log.info("=== CIRMS DataInitializer complete ===");
    }

    // ─────────────────────────────────────────────────────────────
    // Admin account
    // ─────────────────────────────────────────────────────────────

    private void initAdminUser() {
        if (!userRepository.existsByEmail(adminEmail)) {
            log.info("Creating default development admin account: {}", adminEmail);
            userRepository.save(User.builder()
                    .fullName(adminName)
                    .studentId("ADMIN001")
                    .email(adminEmail.toLowerCase())
                    .phone("1234567890")
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .build());
            log.info("Admin account created.");
        } else {
            log.info("Admin account already exists ({})", adminEmail);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // Demo students
    // ─────────────────────────────────────────────────────────────

    private User initDemoStudent(String name, String studentId, String email, String phone) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            log.info("Creating demo student: {}", email);
            return userRepository.save(User.builder()
                    .fullName(name)
                    .studentId(studentId)
                    .email(email)
                    .phone(phone)
                    .password(passwordEncoder.encode("Demo@12345"))
                    .role(Role.STUDENT)
                    .build());
        });
    }

    // ─────────────────────────────────────────────────────────────
    // Seed items — realistic campus scenarios
    //
    // Matching pairs (intentional high-score):
    //  A) Black Dell Laptop — Library (LOST by Aryan) ↔ Black Dell Laptop — Library (FOUND by Priya)
    //  B) Blue Casio Watch — Canteen (LOST by Priya) ↔ Blue Casio Watch — Cafeteria (FOUND by Aryan)
    //  C) Bag — CS Block (LOST by Aryan) ↔ Bag — CS Block (FOUND by Rahul)
    //  D) ID Card — Hostel (LOST by Rahul) ↔ ID Card — Hostel (FOUND by Priya)
    //  E) Earphones — Lab (LOST by Priya) ↔ Earphones — Lab (FOUND by Rahul)
    // ─────────────────────────────────────────────────────────────

    private void seedItems(User s1, User s2, User s3) {
        Category electronics = findCat("Electronics");
        Category accessories = findCat("Accessories");
        Category bags        = findCat("Bags");
        Category idCard      = findCat("ID Card");
        Category books       = findCat("Books");
        Category keys        = findCat("Keys");
        Category wallet      = findCat("Wallet");
        Category clothing    = findCat("Clothing");

        LocalDate today   = LocalDate.now();
        LocalDate minus2  = today.minusDays(2);
        LocalDate minus5  = today.minusDays(5);
        LocalDate minus7  = today.minusDays(7);
        LocalDate minus10 = today.minusDays(10);
        LocalDate minus15 = today.minusDays(15);

        // ── Pair A: Dell Laptop ──────────────────────────────────────────────
        itemRepository.save(Item.builder()
                .user(s1).category(electronics)
                .itemName("Dell XPS 15 Laptop")
                .description("Silver Dell XPS 15 laptop, 15.6-inch. Has a small dent on the bottom left corner and a Python sticker on the lid. Charger was also in the bag.")
                .color("Silver").brand("Dell")
                .location("Central Library, 2nd Floor")
                .dateLostOrFound(minus5)
                .itemType(ItemType.LOST).status(ItemStatus.LOST)
                .build());

        itemRepository.save(Item.builder()
                .user(s2).category(electronics)
                .itemName("Dell Laptop")
                .description("Found a silver Dell laptop near the study tables on the 2nd floor of the library. Has a dent at the bottom. Left at the front desk.")
                .color("Silver").brand("Dell")
                .location("Central Library, 2nd Floor Reading Area")
                .dateLostOrFound(minus5)
                .itemType(ItemType.FOUND).status(ItemStatus.FOUND)
                .build());

        // ── Pair B: Casio Watch ──────────────────────────────────────────────
        itemRepository.save(Item.builder()
                .user(s2).category(accessories)
                .itemName("Casio Digital Watch")
                .description("Blue digital Casio wristwatch, model W-59. Has a small scratch on the glass face. Strap is rubber/black combination.")
                .color("Blue").brand("Casio")
                .location("Main Cafeteria, near the entrance")
                .dateLostOrFound(minus2)
                .itemType(ItemType.LOST).status(ItemStatus.LOST)
                .build());

        itemRepository.save(Item.builder()
                .user(s1).category(accessories)
                .itemName("Casio Watch")
                .description("Found a blue Casio digital watch on a table in the cafeteria. Nobody claimed it. Dropped it at the lost and found box near security.")
                .color("Blue").brand("Casio")
                .location("Cafeteria, near entrance")
                .dateLostOrFound(minus2)
                .itemType(ItemType.FOUND).status(ItemStatus.FOUND)
                .build());

        // ── Pair C: Backpack ─────────────────────────────────────────────────
        itemRepository.save(Item.builder()
                .user(s1).category(bags)
                .itemName("Black Wildcraft Backpack")
                .description("Black 40L Wildcraft rucksack with orange zippers. Contains notebooks, a pencil case, and a water bottle. Name tag with 'Aryan' is attached inside.")
                .color("Black").brand("Wildcraft")
                .location("CS Department Block, Ground Floor Corridor")
                .dateLostOrFound(minus7)
                .itemType(ItemType.LOST).status(ItemStatus.LOST)
                .build());

        itemRepository.save(Item.builder()
                .user(s3).category(bags)
                .itemName("Wildcraft Backpack")
                .description("Found a large black Wildcraft bag with orange zippers in the CS building corridor. Has notebooks inside. Left it at the department office.")
                .color("Black").brand("Wildcraft")
                .location("CS Block Corridor")
                .dateLostOrFound(minus7)
                .itemType(ItemType.FOUND).status(ItemStatus.FOUND)
                .build());

        // ── Pair D: ID Card ──────────────────────────────────────────────────
        itemRepository.save(Item.builder()
                .user(s3).category(idCard)
                .itemName("Student ID Card")
                .description("University student ID card with photo. Belongs to a 3rd year student from the Mechanical Engineering department. Also had a library card attached.")
                .color("White").brand(null)
                .location("Boys Hostel Block B, 1st Floor")
                .dateLostOrFound(minus10)
                .itemType(ItemType.LOST).status(ItemStatus.LOST)
                .build());

        itemRepository.save(Item.builder()
                .user(s2).category(idCard)
                .itemName("Student ID Card")
                .description("Found a student ID card with a library card in Hostel Block B staircase. Left it with the hostel warden.")
                .color("White").brand(null)
                .location("Hostel Block B, near staircase")
                .dateLostOrFound(minus10)
                .itemType(ItemType.FOUND).status(ItemStatus.FOUND)
                .build());

        // ── Pair E: Earphones ────────────────────────────────────────────────
        itemRepository.save(Item.builder()
                .user(s2).category(electronics)
                .itemName("boAt Bassheads 100 Earphones")
                .description("Red boAt wired earphones, BassHeads 100 model. Left them on the desk near PC #12 in the computer lab.")
                .color("Red").brand("boAt")
                .location("Computer Lab, Room 203")
                .dateLostOrFound(minus2)
                .itemType(ItemType.LOST).status(ItemStatus.LOST)
                .build());

        itemRepository.save(Item.builder()
                .user(s3).category(electronics)
                .itemName("boAt Earphones")
                .description("Found red boAt wired earphones next to a desktop in the computer lab, 2nd floor. Nobody came back for them.")
                .color("Red").brand("boAt")
                .location("Computer Lab Room 203, 2nd Floor")
                .dateLostOrFound(minus2)
                .itemType(ItemType.FOUND).status(ItemStatus.FOUND)
                .build());

        // ── Standalone: Non-matching extra items ─────────────────────────────
        itemRepository.save(Item.builder()
                .user(s3).category(keys)
                .itemName("Car Keys with Honda Logo")
                .description("A set of car keys with Honda logo keychain and a small yellow rubber duck attached. Found in the parking lot near Gate 3.")
                .color("Silver").brand("Honda")
                .location("Main Parking Lot, Gate 3")
                .dateLostOrFound(minus15)
                .itemType(ItemType.FOUND).status(ItemStatus.FOUND)
                .build());

        itemRepository.save(Item.builder()
                .user(s1).category(wallet)
                .itemName("Brown Leather Wallet")
                .description("Brown leather bifold wallet. Contains some cash, a debit card, and a driving licence. Lost near the sports complex.")
                .color("Brown").brand(null)
                .location("Sports Complex, near the basketball court")
                .dateLostOrFound(minus15)
                .itemType(ItemType.LOST).status(ItemStatus.LOST)
                .build());

        log.info("Seeded 12 campus items across 5 matching pairs + 2 standalone items.");
    }

    // ─────────────────────────────────────────────────────────────
    // Helper: find category by name or fall back to "Other"
    // ─────────────────────────────────────────────────────────────

    private Category findCat(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.findByName("Other")
                        .orElseThrow(() -> new RuntimeException("Category 'Other' not found. Run category seeder first.")));
    }
}
