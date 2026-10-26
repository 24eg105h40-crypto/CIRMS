package com.cirms.repository;

import com.cirms.entity.Item;
import com.cirms.entity.ItemStatus;
import com.cirms.entity.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByUserId(Long userId);

    List<Item> findByItemType(ItemType itemType);

    List<Item> findByStatus(ItemStatus status);

    List<Item> findByItemTypeAndStatus(ItemType itemType, ItemStatus status);

    long countByUserIdAndItemType(Long userId, ItemType itemType);

    long countByUserIdAndStatus(Long userId, ItemStatus status);

    long countByItemType(ItemType itemType);

    long countByStatus(ItemStatus status);

    @Query("SELECT i FROM Item i WHERE " +
           "(:type IS NULL OR i.itemType = :type) AND " +
           "(:status IS NULL OR i.status = :status) AND " +
           "(:categoryId IS NULL OR i.category.id = :categoryId) AND " +
           "(:search IS NULL OR LOWER(i.itemName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:location IS NULL OR LOWER(i.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:color IS NULL OR LOWER(i.color) LIKE LOWER(CONCAT('%', :color, '%'))) AND " +
           "(:brand IS NULL OR LOWER(i.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
           "(:fromDate IS NULL OR i.dateLostOrFound >= :fromDate) AND " +
           "(:toDate IS NULL OR i.dateLostOrFound <= :toDate)")
    Page<Item> searchItems(
            @Param("type") ItemType type,
            @Param("status") ItemStatus status,
            @Param("categoryId") Long categoryId,
            @Param("search") String search,
            @Param("location") String location,
            @Param("color") String color,
            @Param("brand") String brand,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );
}
