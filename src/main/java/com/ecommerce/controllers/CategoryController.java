package com.ecommerce.controllers;

import com.ecommerce.models.Category;
import com.ecommerce.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {

    CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/api/public/Categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategories());
    }

    @PostMapping("/api/public/Categories")
    public ResponseEntity<String> addCategory(@Valid @RequestBody Category category) {
        categoryService.addCategory(category);
        return ResponseEntity.ok("Successfully created category");
    }

    @DeleteMapping("/api/admin/Categories/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable long categoryId){
        String response = categoryService.deleteCategory(categoryId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/admin/Categories/{categoryId}")
    public ResponseEntity<String> updateCategory(@Valid @PathVariable long categoryId,
                                                 @RequestBody Category category){
            String response = categoryService.updateCategory(categoryId, category);
            return ResponseEntity.ok(response);
    }

}
