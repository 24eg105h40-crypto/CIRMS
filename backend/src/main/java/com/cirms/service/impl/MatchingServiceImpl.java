package com.cirms.service.impl;

import com.cirms.dto.MatchDTO;
import com.cirms.entity.Item;
import com.cirms.entity.ItemStatus;
import com.cirms.entity.ItemType;
import com.cirms.repository.ItemRepository;
import com.cirms.service.MatchingService;
import com.cirms.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Smart attribute-based matching engine.
 *
 * Scoring weights (total max = 100 points):
 *  ┌─────────────────┬────────┬────────────────────────────────────────────────┐
 *  │ Attribute       │ Points │ Logic                                          │
 *  ├─────────────────┼────────┼────────────────────────────────────────────────┤
 *  │ Category        │  30    │ Exact category ID match                        │
 *  │ Name keywords   │  25    │ Jaccard similarity of word tokens (≥0.25→25)   │
 *  │ Color           │  15    │ Case-insensitive exact match                   │
 *  │ Brand           │  15    │ Case-insensitive exact match                   │
 *  │ Location        │  10    │ Word overlap on location tokens (≥1 word→10)   │
 *  │ Date proximity  │   5    │ ≤7 days apart=5, ≤30 days=2, else 0           │
 *  └─────────────────┴────────┴────────────────────────────────────────────────┘
 *
 * A match is surfaced only when totalScore ≥ 25.
 * Notifications are sent when score ≥ 40 (high confidence).
 */
@Service
public class MatchingServiceImpl implements MatchingService {

    private static final Logger log = LoggerFactory.getLogger(MatchingServiceImpl.class);

    /** Minimum score to include in results */
    private static final int MIN_SCORE_THRESHOLD = 25;
    /** Minimum score to trigger a push notification */
    private static final int NOTIFY_SCORE_THRESHOLD = 40;

    private final ItemRepository itemRepository;
    private final NotificationService notificationService;

    public MatchingServiceImpl(ItemRepository itemRepository,
                               @Lazy NotificationService notificationService) {
        this.itemRepository = itemRepository;
        this.notificationService = notificationService;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<MatchDTO> findMatches(Item sourceItem, int maxResults) {
        // Determine the opposing bucket to scan
        ItemType targetType = (sourceItem.getItemType() == ItemType.LOST) ? ItemType.FOUND : ItemType.LOST;
        // Only match against ACTIVE items (LOST or FOUND status)
        ItemStatus targetStatus = (targetType == ItemType.LOST) ? ItemStatus.LOST : ItemStatus.FOUND;

        List<Item> candidates = itemRepository.findByItemTypeAndStatus(targetType, targetStatus);

        return candidates.stream()
                .filter(c -> !c.getId().equals(sourceItem.getId()))            // skip self
                .filter(c -> !c.getUser().getId().equals(sourceItem.getUser().getId())) // skip own items
                .map(candidate -> scoreMatch(sourceItem, candidate))
                .filter(m -> m.getScore() >= MIN_SCORE_THRESHOLD)
                .sorted(Comparator.comparingInt(MatchDTO::getScore).reversed())
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    @Override
    @Async
    @Transactional
    public void triggerMatchingAndNotify(Item newItem) {
        try {
            if (newItem == null || newItem.getId() == null) {
                return;
            }
            // Re-fetch item in the async thread to ensure it is managed by the current persistence context
            Item sourceItem = itemRepository.findById(newItem.getId()).orElse(newItem);
            if (sourceItem.getUser() == null || sourceItem.getUser().getId() == null) {
                return;
            }
            Long userId = sourceItem.getUser().getId();

            List<MatchDTO> matches = findMatches(sourceItem, 5);
            if (matches.isEmpty()) return;

            // Notify the reporter about high-confidence matches
            matches.stream()
                    .filter(m -> m.getScore() >= NOTIFY_SCORE_THRESHOLD)
                    .forEach(match -> {
                        try {
                            String typeWord = sourceItem.getItemType() == ItemType.LOST ? "lost" : "found";
                            String oppositeWord = sourceItem.getItemType() == ItemType.LOST ? "found" : "lost";
                            String title = "🔍 Potential Match Found — " + match.getScore() + "% confidence";
                            String message = String.format(
                                    "Your %s item \"%s\" may match a %s item: \"%s\" at %s. Score: %d/100. %s",
                                    typeWord,
                                    sourceItem.getItemName(),
                                    oppositeWord,
                                    match.getMatchedItemName(),
                                    match.getMatchedItemLocation(),
                                    match.getScore(),
                                    match.getMatchSummary()
                            );
                            notificationService.createNotification(
                                    userId,
                                    title,
                                    message,
                                    "ITEM_MATCH"
                            );
                        } catch (Exception ex) {
                            log.warn("[Matching] Could not send match notification for item #{}: {}", sourceItem.getId(), ex.getMessage());
                        }
                    });

            log.info("[Matching] Item #{} '{}': {} match(es) found, {} notification(s) sent.",
                    sourceItem.getId(), sourceItem.getItemName(), matches.size(),
                    matches.stream().filter(m -> m.getScore() >= NOTIFY_SCORE_THRESHOLD).count());
        } catch (Exception e) {
            log.error("[Matching] Error during async matching for item #{}: {}", newItem != null ? newItem.getId() : null, e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Core Scoring Engine
    // ─────────────────────────────────────────────────────────────────────────

    private MatchDTO scoreMatch(Item source, Item candidate) {
        int categoryScore = scoreCategory(source, candidate);
        int nameScore     = scoreName(source, candidate);
        int colorScore    = scoreColor(source, candidate);
        int brandScore    = scoreBrand(source, candidate);
        int locationScore = scoreLocation(source, candidate);
        int dateScore     = scoreDate(source, candidate);

        int total = categoryScore + nameScore + colorScore + brandScore + locationScore + dateScore;

        String summary = buildSummary(categoryScore, nameScore, colorScore, brandScore, locationScore, dateScore);

        Long reporterId = null;
        String reporterName = null;
        try {
            if (candidate.getUser() != null) {
                reporterId = candidate.getUser().getId();
                reporterName = candidate.getUser().getFullName();
            }
        } catch (Exception ignored) {}

        return MatchDTO.builder()
                .matchedItemId(candidate.getId())
                .matchedItemName(candidate.getItemName())
                .matchedItemImageUrl(candidate.getImageUrl())
                .matchedItemLocation(candidate.getLocation())
                .matchedItemDate(candidate.getDateLostOrFound())
                .matchedItemCategory(candidate.getCategory() != null ? candidate.getCategory().getName() : null)
                .matchedItemColor(candidate.getColor())
                .matchedItemBrand(candidate.getBrand())
                .matchedItemType(candidate.getItemType())
                .matchedItemStatus(candidate.getStatus())
                .matchedItemReporterId(reporterId)
                .matchedItemReporterName(reporterName)
                .score(Math.min(total, 100))
                .matchSummary(summary)
                .categoryScore(categoryScore)
                .nameScore(nameScore)
                .colorScore(colorScore)
                .brandScore(brandScore)
                .locationScore(locationScore)
                .dateScore(dateScore)
                .build();
    }

    // ── Attribute scorers ──────────────────────────────────────────────────

    /** Category: 30 pts for exact category match */
    private int scoreCategory(Item a, Item b) {
        if (a.getCategory() == null || b.getCategory() == null) return 0;
        return a.getCategory().getId().equals(b.getCategory().getId()) ? 30 : 0;
    }

    /**
     * Name: Jaccard similarity of lowercased word tokens.
     * ≥0.60 → 25 pts, ≥0.40 → 20 pts, ≥0.25 → 12 pts, else 0.
     */
    private int scoreName(Item a, Item b) {
        Set<String> tokensA = tokenize(a.getItemName());
        Set<String> tokensB = tokenize(b.getItemName());
        double jaccard = jaccardSimilarity(tokensA, tokensB);
        if (jaccard >= 0.60) return 25;
        if (jaccard >= 0.40) return 20;
        if (jaccard >= 0.25) return 12;
        return 0;
    }

    /** Color: 15 pts for case-insensitive exact match */
    private int scoreColor(Item a, Item b) {
        if (a.getColor() == null || b.getColor() == null) return 0;
        return a.getColor().trim().equalsIgnoreCase(b.getColor().trim()) ? 15 : 0;
    }

    /** Brand: 15 pts for case-insensitive exact match */
    private int scoreBrand(Item a, Item b) {
        if (a.getBrand() == null || b.getBrand() == null) return 0;
        return a.getBrand().trim().equalsIgnoreCase(b.getBrand().trim()) ? 15 : 0;
    }

    /**
     * Location: 10 pts if at least one meaningful word token overlaps.
     * Ignores very short stop-words.
     */
    private int scoreLocation(Item a, Item b) {
        if (a.getLocation() == null || b.getLocation() == null) return 0;
        Set<String> locA = tokenize(a.getLocation());
        Set<String> locB = tokenize(b.getLocation());
        long shared = locA.stream().filter(locB::contains).filter(t -> t.length() > 2).count();
        return shared >= 1 ? 10 : 0;
    }

    /**
     * Date proximity:
     *  ≤7 days → 5 pts
     *  ≤30 days → 2 pts
     *  else → 0
     */
    private int scoreDate(Item a, Item b) {
        if (a.getDateLostOrFound() == null || b.getDateLostOrFound() == null) return 0;
        long diff = Math.abs(ChronoUnit.DAYS.between(a.getDateLostOrFound(), b.getDateLostOrFound()));
        if (diff <= 7)  return 5;
        if (diff <= 30) return 2;
        return 0;
    }

    // ── Utilities ─────────────────────────────────────────────────────────

    private Set<String> tokenize(String text) {
        if (text == null || text.isBlank()) return Collections.emptySet();
        return Arrays.stream(text.toLowerCase().split("[^a-z0-9]+"))
                .filter(t -> t.length() > 1)
                .collect(Collectors.toSet());
    }

    private double jaccardSimilarity(Set<String> a, Set<String> b) {
        if (a.isEmpty() && b.isEmpty()) return 0.0;
        Set<String> intersection = new HashSet<>(a);
        intersection.retainAll(b);
        Set<String> union = new HashSet<>(a);
        union.addAll(b);
        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }

    private String buildSummary(int cat, int name, int color, int brand, int loc, int date) {
        List<String> hits = new ArrayList<>();
        if (cat   > 0) hits.add("Category");
        if (name  > 0) hits.add("Name");
        if (color > 0) hits.add("Color");
        if (brand > 0) hits.add("Brand");
        if (loc   > 0) hits.add("Location");
        if (date  > 0) hits.add("Date");
        return hits.isEmpty() ? "No strong attribute match" : "Matched on: " + String.join(", ", hits);
    }
}
