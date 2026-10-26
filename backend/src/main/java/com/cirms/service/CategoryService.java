package com.cirms.service;

import com.cirms.dto.CategoryDTO;
import com.cirms.entity.Category;

import java.util.List;

public interface CategoryService {
    List<CategoryDTO> getAllCategories();
    CategoryDTO getCategoryById(Long id);
    Category getCategoryEntityById(Long id);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    void initDefaultCategories();
}
