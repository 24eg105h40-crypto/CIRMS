package com.cirms.service.impl;

import com.cirms.dto.ItemCreateRequest;
import com.cirms.dto.ItemDTO;
import com.cirms.dto.ItemUpdateRequest;
import com.cirms.entity.*;
import com.cirms.exception.BadRequestException;
import com.cirms.exception.ResourceNotFoundException;
import com.cirms.repository.CategoryRepository;
import com.cirms.repository.ItemRepository;
import com.cirms.service.CategoryService;
import com.cirms.service.FileStorageService;
import com.cirms.service.ItemService;
import com.cirms.service.MatchingService;
import com.cirms.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final MatchingService matchingService;

    public ItemServiceImpl(ItemRepository itemRepository,
                           CategoryRepository categoryRepository,
                           CategoryService categoryService,
                           UserService userService,
                           FileStorageService fileStorageService,
                           @Lazy MatchingService matchingService) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.categoryService = categoryService;
        this.userService = userService;
        this.fileStorageService = fileStorageService;
        this.matchingService = matchingService;
    }

    @Override
    @Transactional
    public ItemDTO createLostItem(ItemCreateRequest request, MultipartFile image, Long userId) {
        return createItemInternal(request, image, userId, ItemType.LOST, ItemStatus.LOST);
    }

    @Override
    @Transactional
    public ItemDTO createFoundItem(ItemCreateRequest request, MultipartFile image, Long userId) {
        return createItemInternal(request, image, userId, ItemType.FOUND, ItemStatus.FOUND);
    }

    private ItemDTO createItemInternal(ItemCreateRequest request, MultipartFile image, Long userId, ItemType itemType, ItemStatus initialStatus) {
        if (request == null) {
            throw new BadRequestException("Request body cannot be empty");
        }

        validateItemCreateRequest(request);

        User user = userService.getUserEntityById(userId);
        Category category = resolveCategory(request.getCategoryId(), request.getCategory());

        // Handle image upload if provided
        MultipartFile effectiveImage = (image != null && !image.isEmpty()) ? image : request.getImage();
        String imageUrl = request.getImageUrl();
        if (effectiveImage != null && !effectiveImage.isEmpty()) {
            imageUrl = fileStorageService.storeFile(effectiveImage);
        }

        LocalDate effectiveDate = request.resolveDate();
        String effectiveTime = request.resolveTime();

        Item item = Item.builder()
                .user(user)
                .category(category)
                .itemName(request.getItemName().trim())
                .description(request.getDescription().trim())
                .color(request.getColor() != null ? request.getColor().trim() : null)
                .brand(request.getBrand() != null ? request.getBrand().trim() : null)
                .location(request.getLocation().trim())
                .dateLostOrFound(effectiveDate)
                .timeLostOrFound(effectiveTime)
                .itemType(itemType)
                .imageUrl(imageUrl)
                .status(initialStatus)
                .additionalInformation(request.getAdditionalInformation() != null ? request.getAdditionalInformation().trim() : null)
                .build();

        Item saved = itemRepository.save(item);

        // Async: trigger matching and notifications strictly after successful transaction commit
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    matchingService.triggerMatchingAndNotify(saved);
                }
            });
        } else {
            matchingService.triggerMatchingAndNotify(saved);
        }

        return mapToDTO(saved);
    }

    private void validateItemCreateRequest(ItemCreateRequest request) {
        if (request.getItemName() == null || request.getItemName().trim().isBlank()) {
            throw new BadRequestException("Item name is required");
        }
        if (request.getItemName().trim().length() < 2 || request.getItemName().trim().length() > 150) {
            throw new BadRequestException("Item name must be between 2 and 150 characters");
        }

        if (request.getCategoryId() == null && (request.getCategory() == null || request.getCategory().trim().isBlank())) {
            throw new BadRequestException("Category is required");
        }

        if (request.getDescription() == null || request.getDescription().trim().isBlank()) {
            throw new BadRequestException("Description is required");
        }
        if (request.getDescription().trim().length() < 5 || request.getDescription().trim().length() > 3000) {
            throw new BadRequestException("Description must be between 5 and 3000 characters");
        }

        if (request.getLocation() == null || request.getLocation().trim().isBlank()) {
            throw new BadRequestException("Location is required");
        }
        if (request.getLocation().trim().length() < 2 || request.getLocation().trim().length() > 200) {
            throw new BadRequestException("Location must be between 2 and 200 characters");
        }

        if (request.resolveDate() == null) {
            throw new BadRequestException("Date is required");
        }

        if (request.getColor() != null && request.getColor().length() > 50) {
            throw new BadRequestException("Color must not exceed 50 characters");
        }

        if (request.getBrand() != null && request.getBrand().length() > 100) {
            throw new BadRequestException("Brand must not exceed 100 characters");
        }

        if (request.getAdditionalInformation() != null && request.getAdditionalInformation().length() > 2000) {
            throw new BadRequestException("Additional information must not exceed 2000 characters");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDTO getItemById(Long id) {
        return mapToDTO(getItemEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Item getItemEntityById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> getAllItems() {
        return itemRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> getMyReports(Long userId) {
        return itemRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> getItemsByUserId(Long userId) {
        return itemRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDTO> getItemsByType(ItemType type) {
        return itemRepository.findByItemType(type).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDTO updateItem(Long id, ItemUpdateRequest request, MultipartFile image, Long userId) {
        Item item = getItemEntityById(id);

        // Security check: Only original reporter can update their own item
        if (item.getUser() == null || !item.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to update this item report");
        }

        if (request != null) {
            if (request.getItemName() != null) {
                if (request.getItemName().trim().isBlank()) {
                    throw new BadRequestException("Item name cannot be empty");
                }
                if (request.getItemName().trim().length() < 2 || request.getItemName().trim().length() > 150) {
                    throw new BadRequestException("Item name must be between 2 and 150 characters");
                }
                item.setItemName(request.getItemName().trim());
            }

            if (request.getDescription() != null) {
                if (request.getDescription().trim().isBlank()) {
                    throw new BadRequestException("Description cannot be empty");
                }
                if (request.getDescription().trim().length() < 5 || request.getDescription().trim().length() > 3000) {
                    throw new BadRequestException("Description must be between 5 and 3000 characters");
                }
                item.setDescription(request.getDescription().trim());
            }

            if (request.getLocation() != null) {
                if (request.getLocation().trim().isBlank()) {
                    throw new BadRequestException("Location cannot be empty");
                }
                if (request.getLocation().trim().length() < 2 || request.getLocation().trim().length() > 200) {
                    throw new BadRequestException("Location must be between 2 and 200 characters");
                }
                item.setLocation(request.getLocation().trim());
            }

            if (request.getCategoryId() != null || (request.getCategory() != null && !request.getCategory().isBlank())) {
                Category category = resolveCategory(request.getCategoryId(), request.getCategory());
                item.setCategory(category);
            }

            if (request.resolveDate() != null) {
                item.setDateLostOrFound(request.resolveDate());
            }

            if (request.resolveTime() != null) {
                item.setTimeLostOrFound(request.resolveTime());
            }

            if (request.getColor() != null) {
                item.setColor(request.getColor().trim());
            }

            if (request.getBrand() != null) {
                item.setBrand(request.getBrand().trim());
            }

            if (request.getAdditionalInformation() != null) {
                item.setAdditionalInformation(request.getAdditionalInformation().trim());
            }

            if (Boolean.TRUE.equals(request.getRemoveImage())) {
                if (item.getImageUrl() != null) {
                    fileStorageService.deleteFile(item.getImageUrl());
                    item.setImageUrl(null);
                }
            } else if (request.getImageUrl() != null) {
                item.setImageUrl(request.getImageUrl());
            }
        }

        // Handle new image upload
        MultipartFile effectiveImage = (image != null && !image.isEmpty()) ? image : (request != null ? request.getImage() : null);
        if (effectiveImage != null && !effectiveImage.isEmpty()) {
            if (item.getImageUrl() != null) {
                fileStorageService.deleteFile(item.getImageUrl());
            }
            String newImageUrl = fileStorageService.storeFile(effectiveImage);
            item.setImageUrl(newImageUrl);
        }

        Item saved = itemRepository.save(item);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public void deleteItem(Long id, Long userId) {
        Item item = getItemEntityById(id);

        // Security check: Only original reporter can delete their own item
        if (item.getUser() == null || !item.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to delete this item report");
        }

        // Clean up stored image file if exists
        if (item.getImageUrl() != null && !item.getImageUrl().isBlank()) {
            fileStorageService.deleteFile(item.getImageUrl());
        }

        itemRepository.delete(item);
    }

    @Override
    @Transactional
    public ItemDTO updateItemStatus(Long id, ItemStatus status) {
        Item item = getItemEntityById(id);
        item.setStatus(status);
        Item updated = itemRepository.save(item);
        return mapToDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemDTO> searchItems(ItemType type, ItemStatus status, Long categoryId, String search,
                                     String location, String color, String brand,
                                     LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        Page<Item> itemPage = itemRepository.searchItems(type, status, categoryId, search, location, color, brand, fromDate, toDate, pageable);
        return itemPage.map(this::mapToDTO);
    }

    private Category resolveCategory(Long categoryId, String categoryName) {
        if (categoryId != null) {
            return categoryService.getCategoryEntityById(categoryId);
        }
        if (categoryName != null && !categoryName.isBlank()) {
            try {
                Long parsedId = Long.parseLong(categoryName.trim());
                return categoryService.getCategoryEntityById(parsedId);
            } catch (NumberFormatException ignored) {}
            return categoryRepository.findByName(categoryName.trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryName));
        }
        throw new BadRequestException("Category is required");
    }

    private ItemDTO mapToDTO(Item item) {
        return ItemDTO.builder()
                .id(item.getId())
                .userId(item.getUser() != null ? item.getUser().getId() : null)
                .reporterName(item.getUser() != null ? item.getUser().getFullName() : null)
                .categoryId(item.getCategory() != null ? item.getCategory().getId() : null)
                .categoryName(item.getCategory() != null ? item.getCategory().getName() : null)
                .itemName(item.getItemName())
                .description(item.getDescription())
                .color(item.getColor())
                .brand(item.getBrand())
                .location(item.getLocation())
                .dateLostOrFound(item.getDateLostOrFound())
                .timeLostOrFound(item.getTimeLostOrFound())
                .itemType(item.getItemType())
                .imageUrl(item.getImageUrl())
                .status(item.getStatus())
                .additionalInformation(item.getAdditionalInformation())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }
}
