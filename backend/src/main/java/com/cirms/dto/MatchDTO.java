package com.cirms.dto;

import com.cirms.entity.ItemType;
import com.cirms.entity.ItemStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents a scored item match result returned by the matching algorithm.
 * The 'source' item is the one the user reported; 'matched' is the candidate
 * from the opposite type bucket (LOST→FOUND or FOUND→LOST).
 */
public class MatchDTO {

    /** ID of the matched candidate item */
    private Long matchedItemId;
    private String matchedItemName;
    private String matchedItemImageUrl;
    private String matchedItemLocation;
    private LocalDate matchedItemDate;
    private String matchedItemCategory;
    private String matchedItemColor;
    private String matchedItemBrand;
    private ItemType matchedItemType;
    private ItemStatus matchedItemStatus;
    private Long matchedItemReporterId;
    private String matchedItemReporterName;

    /** Composite score 0–100 */
    private int score;

    /** Human-readable breakdown of which attributes matched */
    private String matchSummary;

    // Breakdown scores (for transparency in the UI)
    private int categoryScore;
    private int nameScore;
    private int colorScore;
    private int brandScore;
    private int locationScore;
    private int dateScore;

    public MatchDTO() {}

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public Long getMatchedItemId() { return matchedItemId; }
    public void setMatchedItemId(Long matchedItemId) { this.matchedItemId = matchedItemId; }

    public String getMatchedItemName() { return matchedItemName; }
    public void setMatchedItemName(String matchedItemName) { this.matchedItemName = matchedItemName; }

    public String getMatchedItemImageUrl() { return matchedItemImageUrl; }
    public void setMatchedItemImageUrl(String matchedItemImageUrl) { this.matchedItemImageUrl = matchedItemImageUrl; }

    public String getMatchedItemLocation() { return matchedItemLocation; }
    public void setMatchedItemLocation(String matchedItemLocation) { this.matchedItemLocation = matchedItemLocation; }

    public LocalDate getMatchedItemDate() { return matchedItemDate; }
    public void setMatchedItemDate(LocalDate matchedItemDate) { this.matchedItemDate = matchedItemDate; }

    public String getMatchedItemCategory() { return matchedItemCategory; }
    public void setMatchedItemCategory(String matchedItemCategory) { this.matchedItemCategory = matchedItemCategory; }

    public String getMatchedItemColor() { return matchedItemColor; }
    public void setMatchedItemColor(String matchedItemColor) { this.matchedItemColor = matchedItemColor; }

    public String getMatchedItemBrand() { return matchedItemBrand; }
    public void setMatchedItemBrand(String matchedItemBrand) { this.matchedItemBrand = matchedItemBrand; }

    public ItemType getMatchedItemType() { return matchedItemType; }
    public void setMatchedItemType(ItemType matchedItemType) { this.matchedItemType = matchedItemType; }

    public ItemStatus getMatchedItemStatus() { return matchedItemStatus; }
    public void setMatchedItemStatus(ItemStatus matchedItemStatus) { this.matchedItemStatus = matchedItemStatus; }

    public Long getMatchedItemReporterId() { return matchedItemReporterId; }
    public void setMatchedItemReporterId(Long matchedItemReporterId) { this.matchedItemReporterId = matchedItemReporterId; }

    public String getMatchedItemReporterName() { return matchedItemReporterName; }
    public void setMatchedItemReporterName(String matchedItemReporterName) { this.matchedItemReporterName = matchedItemReporterName; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getMatchSummary() { return matchSummary; }
    public void setMatchSummary(String matchSummary) { this.matchSummary = matchSummary; }

    public int getCategoryScore() { return categoryScore; }
    public void setCategoryScore(int categoryScore) { this.categoryScore = categoryScore; }

    public int getNameScore() { return nameScore; }
    public void setNameScore(int nameScore) { this.nameScore = nameScore; }

    public int getColorScore() { return colorScore; }
    public void setColorScore(int colorScore) { this.colorScore = colorScore; }

    public int getBrandScore() { return brandScore; }
    public void setBrandScore(int brandScore) { this.brandScore = brandScore; }

    public int getLocationScore() { return locationScore; }
    public void setLocationScore(int locationScore) { this.locationScore = locationScore; }

    public int getDateScore() { return dateScore; }
    public void setDateScore(int dateScore) { this.dateScore = dateScore; }

    // ─── Builder ──────────────────────────────────────────────────────────────

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final MatchDTO obj = new MatchDTO();
        public Builder matchedItemId(Long v)             { obj.matchedItemId = v; return this; }
        public Builder matchedItemName(String v)         { obj.matchedItemName = v; return this; }
        public Builder matchedItemImageUrl(String v)     { obj.matchedItemImageUrl = v; return this; }
        public Builder matchedItemLocation(String v)     { obj.matchedItemLocation = v; return this; }
        public Builder matchedItemDate(LocalDate v)      { obj.matchedItemDate = v; return this; }
        public Builder matchedItemCategory(String v)     { obj.matchedItemCategory = v; return this; }
        public Builder matchedItemColor(String v)        { obj.matchedItemColor = v; return this; }
        public Builder matchedItemBrand(String v)        { obj.matchedItemBrand = v; return this; }
        public Builder matchedItemType(ItemType v)       { obj.matchedItemType = v; return this; }
        public Builder matchedItemStatus(ItemStatus v)   { obj.matchedItemStatus = v; return this; }
        public Builder matchedItemReporterId(Long v)     { obj.matchedItemReporterId = v; return this; }
        public Builder matchedItemReporterName(String v) { obj.matchedItemReporterName = v; return this; }
        public Builder score(int v)                      { obj.score = v; return this; }
        public Builder matchSummary(String v)            { obj.matchSummary = v; return this; }
        public Builder categoryScore(int v)              { obj.categoryScore = v; return this; }
        public Builder nameScore(int v)                  { obj.nameScore = v; return this; }
        public Builder colorScore(int v)                 { obj.colorScore = v; return this; }
        public Builder brandScore(int v)                 { obj.brandScore = v; return this; }
        public Builder locationScore(int v)              { obj.locationScore = v; return this; }
        public Builder dateScore(int v)                  { obj.dateScore = v; return this; }
        public MatchDTO build()                          { return obj; }
    }
}
