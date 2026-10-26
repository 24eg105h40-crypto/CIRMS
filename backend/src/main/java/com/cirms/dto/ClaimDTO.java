package com.cirms.dto;

import com.cirms.entity.ClaimStatus;
import java.time.LocalDateTime;

public class ClaimDTO {
    private Long id;
    private Long itemId;
    private String itemName;
    private String itemImageUrl;
    private Long claimantId;
    private String claimantName;
    private String claimantEmail;
    private String reason;
    private String identifyingInformation;
    private String proofImage;
    private ClaimStatus status;
    private String adminComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ClaimDTO() {}

    public ClaimDTO(Long id, Long itemId, String itemName, String itemImageUrl, Long claimantId, String claimantName, String claimantEmail, String reason, String identifyingInformation, String proofImage, ClaimStatus status, String adminComment, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemImageUrl = itemImageUrl;
        this.claimantId = claimantId;
        this.claimantName = claimantName;
        this.claimantEmail = claimantEmail;
        this.reason = reason;
        this.identifyingInformation = identifyingInformation;
        this.proofImage = proofImage;
        this.status = status;
        this.adminComment = adminComment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long itemId;
        private String itemName;
        private String itemImageUrl;
        private Long claimantId;
        private String claimantName;
        private String claimantEmail;
        private String reason;
        private String identifyingInformation;
        private String proofImage;
        private ClaimStatus status;
        private String adminComment;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder itemId(Long itemId) { this.itemId = itemId; return this; }
        public Builder itemName(String itemName) { this.itemName = itemName; return this; }
        public Builder itemImageUrl(String itemImageUrl) { this.itemImageUrl = itemImageUrl; return this; }
        public Builder claimantId(Long claimantId) { this.claimantId = claimantId; return this; }
        public Builder claimantName(String claimantName) { this.claimantName = claimantName; return this; }
        public Builder claimantEmail(String claimantEmail) { this.claimantEmail = claimantEmail; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder identifyingInformation(String identifyingInformation) { this.identifyingInformation = identifyingInformation; return this; }
        public Builder proofImage(String proofImage) { this.proofImage = proofImage; return this; }
        public Builder status(ClaimStatus status) { this.status = status; return this; }
        public Builder adminComment(String adminComment) { this.adminComment = adminComment; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ClaimDTO build() {
            return new ClaimDTO(id, itemId, itemName, itemImageUrl, claimantId, claimantName, claimantEmail, reason, identifyingInformation, proofImage, status, adminComment, createdAt, updatedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getItemImageUrl() { return itemImageUrl; }
    public void setItemImageUrl(String itemImageUrl) { this.itemImageUrl = itemImageUrl; }

    public Long getClaimantId() { return claimantId; }
    public void setClaimantId(Long claimantId) { this.claimantId = claimantId; }

    public String getClaimantName() { return claimantName; }
    public void setClaimantName(String claimantName) { this.claimantName = claimantName; }

    public String getClaimantEmail() { return claimantEmail; }
    public void setClaimantEmail(String claimantEmail) { this.claimantEmail = claimantEmail; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getIdentifyingInformation() { return identifyingInformation; }
    public void setIdentifyingInformation(String identifyingInformation) { this.identifyingInformation = identifyingInformation; }

    public String getProofImage() { return proofImage; }
    public void setProofImage(String proofImage) { this.proofImage = proofImage; }

    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }

    public String getAdminComment() { return adminComment; }
    public void setAdminComment(String adminComment) { this.adminComment = adminComment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
