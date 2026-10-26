package com.cirms.controller;

import com.cirms.dto.ApiResponse;
import com.cirms.dto.ClaimDTO;
import com.cirms.dto.ClaimReviewRequest;
import com.cirms.dto.ItemDTO;
import com.cirms.dto.UserDTO;
import com.cirms.entity.ClaimStatus;
import com.cirms.entity.ItemStatus;
import com.cirms.entity.ItemType;
import com.cirms.repository.ClaimRepository;
import com.cirms.repository.ItemRepository;
import com.cirms.repository.UserRepository;
import com.cirms.service.ClaimService;
import com.cirms.service.ItemService;
import com.cirms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final ClaimService claimService;
    private final ItemService itemService;
    private final UserService userService;
    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public AdminController(ClaimService claimService,
                           ItemService itemService,
                           UserService userService,
                           ClaimRepository claimRepository,
                           ItemRepository itemRepository,
                           UserRepository userRepository) {
        this.claimService = claimService;
        this.itemService = itemService;
        this.userService = userService;
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // 1. DASHBOARD STATISTICS
    // ==========================================

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalUsers", userRepository.count());
        stats.put("totalItems", itemRepository.count());
        stats.put("totalLostItems", itemRepository.countByItemType(ItemType.LOST));
        stats.put("totalFoundItems", itemRepository.countByItemType(ItemType.FOUND));
        stats.put("pendingClaims", claimRepository.countByStatus(ClaimStatus.PENDING));
        stats.put("approvedClaims", claimRepository.countByStatus(ClaimStatus.APPROVED));
        stats.put("rejectedClaims", claimRepository.countByStatus(ClaimStatus.REJECTED));
        stats.put("recoveredItems", itemRepository.countByStatus(ItemStatus.CLAIM_APPROVED));
        stats.put("activeItems", itemRepository.countByStatus(ItemStatus.LOST) + itemRepository.countByStatus(ItemStatus.FOUND));

        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved successfully", stats));
    }

    // ==========================================
    // 2. CLAIM VERIFICATION QUEUE
    // ==========================================

    @GetMapping("/claims/pending")
    public ResponseEntity<ApiResponse<List<ClaimDTO>>> getPendingClaims() {
        List<ClaimDTO> pending = claimRepository.findByStatus(ClaimStatus.PENDING).stream()
                .map(c -> claimService.getClaimById(c.getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("Pending claims retrieved successfully", pending));
    }

    @GetMapping("/claims")
    public ResponseEntity<ApiResponse<List<ClaimDTO>>> getAllClaims() {
        List<ClaimDTO> claims = claimService.getAllClaims();
        return ResponseEntity.ok(ApiResponse.success("All claims retrieved successfully", claims));
    }

    @PutMapping("/claims/{id}/approve")
    public ResponseEntity<ApiResponse<ClaimDTO>> approveClaim(
            @PathVariable Long id,
            @Valid @RequestBody ClaimReviewRequest request
    ) {
        ClaimDTO approved = claimService.approveClaim(id, request.getAdminComment());
        return ResponseEntity.ok(ApiResponse.success("Claim approved successfully", approved));
    }

    @PutMapping("/claims/{id}/reject")
    public ResponseEntity<ApiResponse<ClaimDTO>> rejectClaim(
            @PathVariable Long id,
            @Valid @RequestBody ClaimReviewRequest request
    ) {
        ClaimDTO rejected = claimService.rejectClaim(id, request.getAdminComment());
        return ResponseEntity.ok(ApiResponse.success("Claim rejected successfully", rejected));
    }

    // ==========================================
    // 3. ITEM CATALOG MANAGEMENT
    // ==========================================

    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<ItemDTO>>> getAllItems() {
        List<ItemDTO> items = itemService.getAllItems();
        return ResponseEntity.ok(ApiResponse.success("All items retrieved successfully", items));
    }

    // ==========================================
    // 4. USER MANAGEMENT
    // ==========================================

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }

    // ==========================================
    // 5. ADMIN PING
    // ==========================================

    @GetMapping("/ping")
    public ResponseEntity<ApiResponse<String>> pingAdmin() {
        return ResponseEntity.ok(ApiResponse.success("Admin authorized", "PONG"));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<String>> adminDashboard() {
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard authorized", "OK"));
    }
}
