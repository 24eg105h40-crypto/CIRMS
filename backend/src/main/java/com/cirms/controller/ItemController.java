package com.cirms.controller;

import com.cirms.dto.ApiResponse;
import com.cirms.dto.ItemCreateRequest;
import com.cirms.dto.ItemDTO;
import com.cirms.dto.ItemUpdateRequest;
import com.cirms.entity.ClaimStatus;
import com.cirms.entity.ItemStatus;
import com.cirms.entity.ItemType;
import com.cirms.exception.UnauthorizedException;
import com.cirms.repository.ClaimRepository;
import com.cirms.security.UserPrincipal;
import com.cirms.service.FileStorageService;
import com.cirms.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;
    private final FileStorageService fileStorageService;
    private final ClaimRepository claimRepository;

    public ItemController(ItemService itemService, FileStorageService fileStorageService, ClaimRepository claimRepository) {
        this.itemService = itemService;
        this.fileStorageService = fileStorageService;
        this.claimRepository = claimRepository;
    }

    // ==========================================
    // 1. REPORT LOST ITEM
    // ==========================================

    @PostMapping(value = "/lost", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ItemDTO>> createLostItemJson(
            @Valid @RequestBody ItemCreateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ensureAuthenticated(currentUser);
        ItemDTO created = itemService.createLostItem(request, null, currentUser.getId());
        return new ResponseEntity<>(ApiResponse.success("Lost item reported successfully", created), HttpStatus.CREATED);
    }

    @PostMapping(value = "/lost", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ItemDTO>> createLostItemMultipart(
            @ModelAttribute ItemCreateRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ensureAuthenticated(currentUser);
        ItemDTO created = itemService.createLostItem(request, image, currentUser.getId());
        return new ResponseEntity<>(ApiResponse.success("Lost item reported successfully", created), HttpStatus.CREATED);
    }

    // ==========================================
    // 2. REPORT FOUND ITEM
    // ==========================================

    @PostMapping(value = "/found", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ItemDTO>> createFoundItemJson(
            @Valid @RequestBody ItemCreateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ensureAuthenticated(currentUser);
        ItemDTO created = itemService.createFoundItem(request, null, currentUser.getId());
        return new ResponseEntity<>(ApiResponse.success("Found item reported successfully", created), HttpStatus.CREATED);
    }

    @PostMapping(value = "/found", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ItemDTO>> createFoundItemMultipart(
            @ModelAttribute ItemCreateRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ensureAuthenticated(currentUser);
        ItemDTO created = itemService.createFoundItem(request, image, currentUser.getId());
        return new ResponseEntity<>(ApiResponse.success("Found item reported successfully", created), HttpStatus.CREATED);
    }

    // ==========================================
    // 3. GET ITEM BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ItemDTO>> getItemById(@PathVariable Long id) {
        ItemDTO item = itemService.getItemById(id);
        return ResponseEntity.ok(ApiResponse.success("Item retrieved successfully", item));
    }

    // ==========================================
    // 4. MY REPORTS (Authenticated User)
    // ==========================================

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ItemDTO>>> getMyReports(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ensureAuthenticated(currentUser);
        List<ItemDTO> myItems = itemService.getMyReports(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("User reports retrieved successfully", myItems));
    }

    // ==========================================
    // 4b. MY STATS (for StudentDashboard KPI cards)
    // ==========================================

    @GetMapping("/my-stats")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getMyStats(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ensureAuthenticated(currentUser);
        Long userId = currentUser.getId();
        Map<String, Long> stats = new HashMap<>();
        stats.put("lostCount", itemService.getMyReports(userId).stream()
                .filter(i -> ItemType.LOST.name().equals(i.getItemType() != null ? i.getItemType().toString() : "")).count());
        stats.put("foundCount", itemService.getMyReports(userId).stream()
                .filter(i -> ItemType.FOUND.name().equals(i.getItemType() != null ? i.getItemType().toString() : "")).count());
        stats.put("pendingClaims", claimRepository.countByClaimantIdAndStatus(userId, ClaimStatus.PENDING));
        stats.put("recoveredCount", claimRepository.countByClaimantIdAndStatus(userId, ClaimStatus.APPROVED));
        return ResponseEntity.ok(ApiResponse.success("Stats retrieved successfully", stats));
    }

    // ==========================================
    // 5. UPDATE ITEM (Owner Only)
    // ==========================================

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<ItemDTO>> updateItemJson(
            @PathVariable Long id,
            @Valid @RequestBody ItemUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ensureAuthenticated(currentUser);
        ItemDTO updated = itemService.updateItem(id, request, null, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Item updated successfully", updated));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ItemDTO>> updateItemMultipart(
            @PathVariable Long id,
            @ModelAttribute ItemUpdateRequest request,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ensureAuthenticated(currentUser);
        ItemDTO updated = itemService.updateItem(id, request, image, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Item updated successfully", updated));
    }

    // ==========================================
    // 6. DELETE ITEM (Owner Only)
    // ==========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteItem(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ensureAuthenticated(currentUser);
        itemService.deleteItem(id, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Item deleted successfully", null));
    }

    // ==========================================
    // 7. GET UPLOADED IMAGE RESOURCE
    // ==========================================

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        Resource file = fileStorageService.loadFileAsResource(filename);

        String contentType = "application/octet-stream";
        try {
            if (file.getFile().exists()) {
                contentType = Files.probeContentType(file.getFile().toPath());
            }
        } catch (IOException ignored) {}

        if (contentType == null || contentType.equals("application/octet-stream")) {
            if (filename.toLowerCase().endsWith(".png")) contentType = "image/png";
            else if (filename.toLowerCase().endsWith(".webp")) contentType = "image/webp";
            else contentType = "image/jpeg";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }

    // ==========================================
    // 8. ALL ITEMS & SEARCH
    // ==========================================

    @GetMapping
    public ResponseEntity<ApiResponse<List<ItemDTO>>> getAllItems() {
        List<ItemDTO> items = itemService.getAllItems();
        return ResponseEntity.ok(ApiResponse.success("Items retrieved successfully", items));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ItemDTO>>> searchItems(
            @RequestParam(required = false) ItemType type,
            @RequestParam(required = false) ItemStatus status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String color,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ItemDTO> results = itemService.searchItems(type, status, categoryId, query, location, color, brand, fromDate, toDate, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search completed successfully", results));
    }

    private void ensureAuthenticated(UserPrincipal currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("User is not authenticated");
        }
    }
}
