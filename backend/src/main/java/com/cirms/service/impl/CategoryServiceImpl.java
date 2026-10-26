package com.cirms.service.impl;

import com.cirms.dto.CategoryDTO;
import com.cirms.entity.Category;
import com.cirms.exception.BadRequestException;
import com.cirms.exception.ResourceNotFoundException;
import com.cirms.repository.CategoryRepository;
import com.cirms.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = getCategoryEntityById(id);
        return mapToDTO(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new BadRequestException("Category already exists with name: " + categoryDTO.getName());
        }

        Category category = Category.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .build();

        Category saved = categoryRepository.save(category);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public void initDefaultCategories() {
        List<String> defaultNames = Arrays.asList(
                "Electronics",
                "Documents",
                "Wallet",
                "ID Card",
                "Books",
                "Keys",
                "Bags",
                "Clothing",
                "Jewellery",
                "Accessories",
                "Other"
        );

        for (String name : defaultNames) {
            if (!categoryRepository.existsByName(name)) {
                categoryRepository.save(Category.builder()
                        .name(name)
                        .description("Items categorized under " + name)
                        .build());
            }
        }
    }

    private CategoryDTO mapToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .itemCount(category.getItems() != null ? (long) category.getItems().size() : 0L)
                .build();
    }
}
