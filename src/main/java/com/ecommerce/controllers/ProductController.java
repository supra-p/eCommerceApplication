package com.ecommerce.controllers;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
            @RequestParam(name="pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(name="pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "productId", required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = "asc", required = false) String sortOrder
    ){
        ProductResponse productResponse = productService.getProducts(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getAllProductsByCategory(
            @RequestParam(name="pageNumber", defaultValue = "0", required = false) Integer pageNumber,
            @RequestParam(name="pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = "productId", required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = "asc", required = false) String sortOrder,
            @PathVariable Long categoryId
    ){
        ProductResponse productResponse = productService.getProductsByCategory(categoryId, pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getAllProductsByKeyword(
            @PathVariable String keyword
    ){
        ProductResponse productResponse = productService.getProductsByKeyword(keyword);
        return ResponseEntity.ok(productResponse);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDto> addProduct(@RequestBody ProductDto productDto,
                                                 @PathVariable Long categoryId){
        ProductDto productDtoNew = productService.addProduct(categoryId, productDto);
        return new ResponseEntity<>(productDtoNew, HttpStatus.CREATED);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@RequestBody ProductDto productDto,
                                                 @PathVariable Long productId){
        ProductDto productDtoNew = productService.updateProduct(productId, productDto);
        return new ResponseEntity<>(productDtoNew, HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId){
        String message = productService.deleteProduct(productId);
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ProductDto> updateProductImage(@RequestParam(name = "image")  MultipartFile image,
                                                         @PathVariable Long productId){
        ProductDto productDtoNew = productService.updateProductImage(productId, image);
        return new ResponseEntity<>(productDtoNew, HttpStatus.OK);
    }


}
