package com.cirms.controller;

import com.cirms.dto.ApiResponse;
import com.cirms.dto.MatchDTO;
import com.cirms.entity.Item;
import com.cirms.exception.UnauthorizedException;
import com.cirms.security.UserPrincipal;
import com.cirms.service.ItemService;
import com.cirms.service.MatchingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchingController {

    private final MatchingService matchingService;
    private final ItemService itemService;

    public MatchingController(MatchingService matchingService, ItemService itemService) {
        this.matchingService = matchingService;
        this.itemService = itemService;
    }

    // ==========================================
    // GET /api/matches/item/{itemId}?maxResults=10
    // Returns potential matches for a given item
    // ==========================================

    @GetMapping("/item/{itemId}")
    public ResponseEntity<ApiResponse<List<MatchDTO>>> getMatchesForItem(
            @PathVariable Long itemId,
            @RequestParam(defaultValue = "10") int maxResults,
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        if (currentUser == null) {
            throw new UnauthorizedException("Authentication required");
        }

        Item item = itemService.getItemEntityById(itemId);
        List<MatchDTO> matches = matchingService.findMatches(item, Math.min(maxResults, 20));
        return ResponseEntity.ok(ApiResponse.success(
                matches.size() + " potential match(es) found",
                matches
        ));
    }

    // ==========================================
    // GET /api/matches/my
    // Returns matches for ALL of the current user's active items
    // ==========================================

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<MatchDTO>>> getMyMatches(
            @AuthenticationPrincipal UserPrincipal currentUser
    ) {
        if (currentUser == null) {
            throw new UnauthorizedException("Authentication required");
        }

        // Aggregate matches across all the user's active items, deduplicate by matchedItemId,
        // keep highest score per matched item, sort descending
        List<MatchDTO> aggregated = itemService.getMyReports(currentUser.getId()).stream()
                .map(dto -> itemService.getItemEntityById(dto.getId()))
                .filter(item -> item.getStatus() != null &&
                        (item.getStatus().name().equals("LOST") || item.getStatus().name().equals("FOUND")))
                .flatMap(item -> matchingService.findMatches(item, 5).stream())
                .collect(java.util.stream.Collectors.toMap(
                        MatchDTO::getMatchedItemId,
                        m -> m,
                        (a, b) -> a.getScore() >= b.getScore() ? a : b
                ))
                .values().stream()
                .sorted(java.util.Comparator.comparingInt(MatchDTO::getScore).reversed())
                .limit(20)
                .collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(
                aggregated.size() + " potential match(es) across your reports",
                aggregated
        ));
    }
}
