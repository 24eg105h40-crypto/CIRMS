package com.cirms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Item is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @NotNull(message = "Claimant is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimant_id", nullable = false)
    private User claimant;

    @NotBlank(message = "Reason for claim is required")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String reason;

    @NotBlank(message = "Identifying information is required")
    @Column(name = "identifying_information", columnDefinition = "TEXT", nullable = false)
    private String identifyingInformation;

    @Column(name = "proof_image")
    private String proofImage;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClaimStatus status = ClaimStatus.PENDING;

    @Column(name = "admin_comment", columnDefinition = "TEXT")
    private String adminComment;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Claim() {
    }

    public Claim(Long id, Item item, User claimant, String reason, String identifyingInformation, String proofImage, ClaimStatus status, String adminComment, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.item = item;
        this.claimant = claimant;
        this.reason = reason;
        this.identifyingInformation = identifyingInformation;
        this.proofImage = proofImage;
        this.status = status != null ? status : ClaimStatus.PENDING;
        this.adminComment = adminComment;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Item item;
        private User claimant;
        private String reason;
        private String identifyingInformation;
        private String proofImage;
        private ClaimStatus status = ClaimStatus.PENDING;
        private String adminComment;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder item(Item item) { this.item = item; return this; }
        public Builder claimant(User claimant) { this.claimant = claimant; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder identifyingInformation(String identifyingInformation) { this.identifyingInformation = identifyingInformation; return this; }
        public Builder proofImage(String proofImage) { this.proofImage = proofImage; return this; }
        public Builder status(ClaimStatus status) { this.status = status; return this; }
        public Builder adminComment(String adminComment) { this.adminComment = adminComment; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Claim build() {
            return new Claim(id, item, claimant, reason, identifyingInformation, proofImage, status, adminComment, createdAt, updatedAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ClaimStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public User getClaimant() { return claimant; }
    public void setClaimant(User claimant) { this.claimant = claimant; }

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
