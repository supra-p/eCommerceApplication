package com.ecommerce.services;

import com.ecommerce.dto.CategoryDto;
import com.ecommerce.dto.CategoryResponse;
import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.resourceNotFoundException;
import com.ecommerce.models.Category;
import com.ecommerce.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService{

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortable = sortOrder.equalsIgnoreCase("asc")
                                            ? Sort.by(sortBy).ascending()
                                            : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortable);
        Page<Category> pages = categoryRepository.findAll(pageDetails);
        List<Category> categories = pages.getContent();
        List<CategoryDto> categoryDtos =  categories.stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .toList();
        CategoryResponse response = new CategoryResponse();
        response.setContent(categoryDtos);
        response.setPageNumber(pageNumber);
        response.setLastPage(pages.isLast());
        response.setTotalElements(pages.getTotalElements());
        response.setTotalPages(pages.getTotalPages());
        response.setPageSize(pages.getSize());
        return response;
    }

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = modelMapper.map(categoryDto, Category.class);
        if(categoryRepository.findByCategoryName(category.getCategoryName()) != null)
            throw new APIException("Category with name " + category.getCategoryName()+ " already exists!!!");
        Category savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory, CategoryDto.class);
    }

    @Override
    public String deleteCategory(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()-> new resourceNotFoundException("Category with ID %d is not found".formatted(id)));
            categoryRepository.delete(category);
            return "Category deleted successfully!";
    }

    @Override
    public String updateCategory(long categoryId, CategoryDto categoryDto) {
        Category category = modelMapper.map(categoryDto,Category.class);
        Category oldCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new resourceNotFoundException("Category with ID %d is not found".formatted(categoryId)));
        oldCategory.setCategoryName(category.getCategoryName());
        categoryRepository.save(oldCategory);
        return "Category updated successfully";
    }
}
