package com.cirms.repository;

import com.cirms.entity.Claim;
import com.cirms.entity.ClaimStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByClaimantId(Long claimantId);

    List<Claim> findByItemId(Long itemId);

    List<Claim> findByStatus(ClaimStatus status);

    long countByClaimantIdAndStatus(Long claimantId, ClaimStatus status);

    long countByStatus(ClaimStatus status);

    boolean existsByItemIdAndClaimantId(Long itemId, Long claimantId);
}
