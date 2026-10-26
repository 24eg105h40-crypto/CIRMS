package com.cirms.service;

import com.cirms.dto.ItemCreateRequest;
import com.cirms.dto.ItemDTO;
import com.cirms.dto.ItemUpdateRequest;
import com.cirms.entity.Item;
import com.cirms.entity.ItemStatus;
import com.cirms.entity.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface ItemService {
    ItemDTO createLostItem(ItemCreateRequest request, MultipartFile image, Long userId);
    ItemDTO createFoundItem(ItemCreateRequest request, MultipartFile image, Long userId);
    ItemDTO getItemById(Long id);
    Item getItemEntityById(Long id);
    List<ItemDTO> getAllItems();
    List<ItemDTO> getMyReports(Long userId);
    List<ItemDTO> getItemsByUserId(Long userId);
    List<ItemDTO> getItemsByType(ItemType type);
    ItemDTO updateItem(Long id, ItemUpdateRequest request, MultipartFile image, Long userId);
    void deleteItem(Long id, Long userId);
    ItemDTO updateItemStatus(Long id, ItemStatus status);
    Page<ItemDTO> searchItems(ItemType type, ItemStatus status, Long categoryId, String search,
                              String location, String color, String brand,
                              LocalDate fromDate, LocalDate toDate, Pageable pageable);
}
