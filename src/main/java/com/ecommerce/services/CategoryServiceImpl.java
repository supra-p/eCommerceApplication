package com.ecommerce.services;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.resourceNotFoundException;
import com.ecommerce.models.Category;
import com.ecommerce.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public void addCategory(Category category) {
        if(categoryRepository.findByCategoryName(category.getCategoryName()) != null)
            throw new APIException("Category with name " + category.getCategoryName()+ " already exists!!!");
        categoryRepository.save(category);
    }

    @Override
    public String deleteCategory(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new resourceNotFoundException("Category with ID %d is not found".formatted(id)));
            categoryRepository.delete(category);
            return "Category deleted successfully!";
    }

    @Override
    public String updateCategory(long categoryId, Category category) {

        Optional<Category> oldCategory = categoryRepository.findById(categoryId);
        if (oldCategory.isPresent()) {
            oldCategory.get().setCategoryName(category.getCategoryName());
            categoryRepository.save(oldCategory.get());
            return "Category updated successfully";
        }
        throw new resourceNotFoundException("Category with ID %d is not found".formatted(categoryId));
    }
}
