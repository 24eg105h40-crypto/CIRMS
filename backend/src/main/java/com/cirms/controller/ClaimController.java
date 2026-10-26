package com.cirms.controller;

import com.cirms.dto.ApiResponse;
import com.cirms.dto.ClaimCreateRequest;
import com.cirms.dto.ClaimDTO;
import com.cirms.dto.ClaimReviewRequest;
import com.cirms.exception.UnauthorizedException;
import com.cirms.security.UserPrincipal;
import com.cirms.service.ClaimService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    // ==========================================
    // 1. SUBMIT A CLAIM (authenticated student)
    // ==========================================

    @PostMapping
    public ResponseEntity<ApiResponse<ClaimDTO>> submitClaim(
            @Valid @RequestBody ClaimCreateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ensureAuthenticated(currentUser);
        ClaimDTO created = claimService.createClaim(request, currentUser.getId());
        return new ResponseEntity<>(ApiResponse.success("Claim submitted successfully", created), HttpStatus.CREATED);
    }

    // ==========================================
    // 2. MY CLAIMS (current user's submissions)
    // ==========================================

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<ClaimDTO>>> getMyClaims(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        ensureAuthenticated(currentUser);
        List<ClaimDTO> claims = claimService.getClaimsByClaimantId(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Your claims retrieved successfully", claims));
    }

    // ==========================================
    // 3. GET ALL CLAIMS (admin utility)
    // ==========================================

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClaimDTO>>> getAllClaims() {
        List<ClaimDTO> claims = claimService.getAllClaims();
        return ResponseEntity.ok(ApiResponse.success("Claims retrieved successfully", claims));
    }

    // ==========================================
    // 4. GET CLAIM BY ID
    // ==========================================

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClaimDTO>> getClaimById(@PathVariable Long id) {
        ClaimDTO claim = claimService.getClaimById(id);
        return ResponseEntity.ok(ApiResponse.success("Claim retrieved successfully", claim));
    }

    // ==========================================
    // 5. GET CLAIMS FOR AN ITEM
    // ==========================================

    @GetMapping("/item/{itemId}")
    public ResponseEntity<ApiResponse<List<ClaimDTO>>> getClaimsByItemId(@PathVariable Long itemId) {
        List<ClaimDTO> claims = claimService.getClaimsByItemId(itemId);
        return ResponseEntity.ok(ApiResponse.success("Item claims retrieved successfully", claims));
    }

    // ==========================================
    // Helper
    // ==========================================

    private void ensureAuthenticated(UserPrincipal currentUser) {
        if (currentUser == null) {
            throw new UnauthorizedException("User is not authenticated");
        }
    }
}
