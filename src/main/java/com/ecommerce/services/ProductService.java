package com.ecommerce.services;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.dto.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    ProductDto addProduct(Long categoryId, ProductDto product);

    ProductResponse getProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    ProductResponse getProductsByKeyword(String keyword);

    ProductDto updateProduct(Long productId, ProductDto product);

    String deleteProduct(Long productId);

    ProductDto updateProductImage(Long productId, MultipartFile image);
}
