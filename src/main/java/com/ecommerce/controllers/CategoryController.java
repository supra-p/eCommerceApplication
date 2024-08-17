package com.ecommerce.controllers;

import com.ecommerce.dto.CategoryDto;
import com.ecommerce.dto.CategoryResponse;
import com.ecommerce.services.CategoryService;
import com.ecommerce.utils.Constants;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CategoryController {

    CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/api/public/Categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber", defaultValue = Constants.PAGE_NUMBER, required = false) int pageNumber,
            @RequestParam(name = "pageSize", defaultValue = Constants.PAGE_SIZE, required = false) int pageSize,
            @RequestParam(name = "sortBy", defaultValue = "categoryId", required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = "asc", required = false) String sortOrder
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategories(pageNumber, pageSize, sortBy, sortOrder));
    }

    @PostMapping("/api/public/Categories")
    public ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto savedCategoryDto = categoryService.addCategory(categoryDto);
        return ResponseEntity.ok(savedCategoryDto);
    }

    @DeleteMapping("/api/admin/Categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable long categoryId){
        String response = categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/admin/Categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@Valid @PathVariable long categoryId,
                                                 @RequestBody CategoryDto categoryDto){
            String response = categoryService.updateCategory(categoryId, categoryDto);
            return ResponseEntity.ok(response);
    }

}
