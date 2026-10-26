package com.cirms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ClaimCreateRequest {

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @NotBlank(message = "Reason for claim is required")
    private String reason;

    @NotBlank(message = "Identifying information is required")
    private String identifyingInformation;

    private String proofImage;

    public ClaimCreateRequest() {}

    public ClaimCreateRequest(Long itemId, String reason, String identifyingInformation, String proofImage) {
        this.itemId = itemId;
        this.reason = reason;
        this.identifyingInformation = identifyingInformation;
        this.proofImage = proofImage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long itemId;
        private String reason;
        private String identifyingInformation;
        private String proofImage;

        public Builder itemId(Long itemId) { this.itemId = itemId; return this; }
        public Builder reason(String reason) { this.reason = reason; return this; }
        public Builder identifyingInformation(String identifyingInformation) { this.identifyingInformation = identifyingInformation; return this; }
        public Builder proofImage(String proofImage) { this.proofImage = proofImage; return this; }

        public ClaimCreateRequest build() {
            return new ClaimCreateRequest(itemId, reason, identifyingInformation, proofImage);
        }
    }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getIdentifyingInformation() { return identifyingInformation; }
    public void setIdentifyingInformation(String identifyingInformation) { this.identifyingInformation = identifyingInformation; }

    public String getProofImage() { return proofImage; }
    public void setProofImage(String proofImage) { this.proofImage = proofImage; }
}
