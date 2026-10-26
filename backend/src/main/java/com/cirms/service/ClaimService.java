package com.cirms.service;

import com.cirms.dto.ClaimCreateRequest;
import com.cirms.dto.ClaimDTO;
import com.cirms.entity.Claim;

import java.util.List;

public interface ClaimService {
    ClaimDTO getClaimById(Long id);
    Claim getClaimEntityById(Long id);
    List<ClaimDTO> getAllClaims();
    List<ClaimDTO> getClaimsByClaimantId(Long claimantId);
    List<ClaimDTO> getClaimsByItemId(Long itemId);
    ClaimDTO createClaim(ClaimCreateRequest request, Long claimantId);
    ClaimDTO approveClaim(Long claimId, String adminComment);
    ClaimDTO rejectClaim(Long claimId, String adminComment);
}
