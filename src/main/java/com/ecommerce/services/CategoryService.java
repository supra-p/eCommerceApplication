package com.ecommerce.services;

import com.ecommerce.dto.CategoryDto;
import com.ecommerce.dto.CategoryResponse;

public interface CategoryService {
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDto addCategory(CategoryDto categoryDto);
    String deleteCategory(long id);
    String updateCategory(long categoryId, CategoryDto categoryDto);
}
