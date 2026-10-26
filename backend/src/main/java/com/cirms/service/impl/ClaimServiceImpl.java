package com.cirms.service.impl;

import com.cirms.dto.ClaimCreateRequest;
import com.cirms.dto.ClaimDTO;
import com.cirms.entity.*;
import com.cirms.exception.BadRequestException;
import com.cirms.exception.ResourceNotFoundException;
import com.cirms.repository.ClaimRepository;
import com.cirms.repository.ItemRepository;
import com.cirms.repository.UserRepository;
import com.cirms.service.ClaimService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClaimServiceImpl implements ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ClaimServiceImpl(ClaimRepository claimRepository, ItemRepository itemRepository, UserRepository userRepository) {
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ClaimDTO getClaimById(Long id) {
        return mapToDTO(getClaimEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Claim getClaimEntityById(Long id) {
        return claimRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Claim not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimDTO> getAllClaims() {
        return claimRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimDTO> getClaimsByClaimantId(Long claimantId) {
        return claimRepository.findByClaimantId(claimantId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimDTO> getClaimsByItemId(Long itemId) {
        return claimRepository.findByItemId(itemId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClaimDTO createClaim(ClaimCreateRequest request, Long claimantId) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + request.getItemId()));

        User claimant = userRepository.findById(claimantId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + claimantId));

        if (item.getItemType() != ItemType.FOUND) {
            throw new BadRequestException("Claims can only be submitted for FOUND items");
        }

        if (item.getUser().getId().equals(claimantId)) {
            throw new BadRequestException("You cannot submit a claim for an item reported by yourself");
        }

        if (claimRepository.existsByItemIdAndClaimantId(item.getId(), claimantId)) {
            throw new BadRequestException("You have already submitted a claim for this item");
        }

        Claim claim = Claim.builder()
                .item(item)
                .claimant(claimant)
                .reason(request.getReason())
                .identifyingInformation(request.getIdentifyingInformation())
                .proofImage(request.getProofImage())
                .status(ClaimStatus.PENDING)
                .build();

        // Update item status to CLAIM_PENDING
        item.setStatus(ItemStatus.CLAIM_PENDING);
        itemRepository.save(item);

        Claim saved = claimRepository.save(claim);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public ClaimDTO approveClaim(Long claimId, String adminComment) {
        Claim claim = getClaimEntityById(claimId);
        claim.setStatus(ClaimStatus.APPROVED);
        claim.setAdminComment(adminComment);

        Item item = claim.getItem();
        item.setStatus(ItemStatus.CLAIM_APPROVED);
        itemRepository.save(item);

        Claim saved = claimRepository.save(claim);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public ClaimDTO rejectClaim(Long claimId, String adminComment) {
        Claim claim = getClaimEntityById(claimId);
        claim.setStatus(ClaimStatus.REJECTED);
        claim.setAdminComment(adminComment);

        Item item = claim.getItem();
        item.setStatus(ItemStatus.FOUND);
        itemRepository.save(item);

        Claim saved = claimRepository.save(claim);
        return mapToDTO(saved);
    }

    private ClaimDTO mapToDTO(Claim claim) {
        return ClaimDTO.builder()
                .id(claim.getId())
                .itemId(claim.getItem() != null ? claim.getItem().getId() : null)
                .itemName(claim.getItem() != null ? claim.getItem().getItemName() : null)
                .itemImageUrl(claim.getItem() != null ? claim.getItem().getImageUrl() : null)
                .claimantId(claim.getClaimant() != null ? claim.getClaimant().getId() : null)
                .claimantName(claim.getClaimant() != null ? claim.getClaimant().getFullName() : null)
                .claimantEmail(claim.getClaimant() != null ? claim.getClaimant().getEmail() : null)
                .reason(claim.getReason())
                .identifyingInformation(claim.getIdentifyingInformation())
                .proofImage(claim.getProofImage())
                .status(claim.getStatus())
                .adminComment(claim.getAdminComment())
                .createdAt(claim.getCreatedAt())
                .updatedAt(claim.getUpdatedAt())
                .build();
    }
}
