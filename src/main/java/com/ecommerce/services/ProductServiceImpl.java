package com.ecommerce.services;

import com.ecommerce.dto.ProductDto;
import com.ecommerce.dto.ProductResponse;
import com.ecommerce.exceptions.resourceNotFoundException;
import com.ecommerce.models.Category;
import com.ecommerce.models.Product;
import com.ecommerce.repositories.CategoryRepository;
import com.ecommerce.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductDto addProduct(Long categoryId, ProductDto productDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new resourceNotFoundException("Category with id %s not found".formatted(categoryId)));
        Product product = modelMapper.map(productDto, Product.class);
        product.setImage("Default.png");
        product.setCategory(category);
        double specialPrice = calculateSpecialPrice(product.getPrice(), product.getDiscount());
        product.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDto.class);
    }

    @Override
    public ProductResponse getProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortable = sortOrder.equalsIgnoreCase("asc")
                            ? Sort.by(sortBy).ascending()
                            : Sort.by(sortBy).descending();
        Pageable pages = PageRequest.of(pageNumber, pageSize, sortable);
        Page<Product> pageProducts = productRepository.findAll(pages);
        return getProductResponse(pageNumber, pageSize, pageProducts);
    }

    @Override
    public ProductResponse getProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize,
                                                 String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                                .orElseThrow(() -> new resourceNotFoundException("Category not found!!"));
        Sort sortable = sortOrder.equalsIgnoreCase("asc")
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending();
        Pageable pages = PageRequest.of(pageNumber, pageSize, sortable);
        Page<Product> pageProducts = productRepository.findAllByCategory(category, pages);
        return getProductResponse(pageNumber, pageSize, pageProducts);
    }

    @Override
    public ProductResponse getProductsByKeyword(String keyword) {
        List<Product> products = productRepository.findByProductNameIgnoreCaseOrDescriptionIgnoreCase(keyword);
        List<ProductDto> productDtos = products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDtos);
        return productResponse;
    }

    @Override
    public ProductDto updateProduct(Long productId, ProductDto productDto) {
        Product productDb = productRepository.findById(productId)
                .orElseThrow(() -> new resourceNotFoundException("Product ID %s not found".formatted(productId)));

        Product product = modelMapper.map(productDto, Product.class);
        productDb.setProductName(product.getProductName() == null ? productDb.getProductName() : product.getProductName());
        productDb.setDescription(product.getDescription() == null ? productDb.getDescription() : product.getDescription());
        productDb.setPrice(product.getPrice() == null ? productDb.getPrice() : product.getPrice());
        productDb.setDiscount(product.getDiscount()== null ? productDb.getDiscount() : product.getDiscount());
        productDb.setQuantity(product.getQuantity() == null ? productDb.getQuantity() : product.getQuantity());
        productDb.setSpecialPrice(calculateSpecialPrice(productDb.getPrice(), productDb.getDiscount()));

        productDb = productRepository.save(productDb);

        return modelMapper.map(productDb, ProductDto.class);
    }

    @Override
    public String deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new resourceNotFoundException("Product not found!!"));
        productRepository.deleteById(productId);
        return "Product has been delete successfully";
    }

    @Override
    public ProductDto updateProductImage(Long productId, MultipartFile image) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new resourceNotFoundException("Product not found!"));

        String path = "images/";
        String filename = uploadImage(path, image);

        product.setImage(filename);
        product = productRepository.save(product);
        return modelMapper.map(product, ProductDto.class);
    }


    // ***************************** HELPER METHODS ********************************


    private String uploadImage(String path, MultipartFile file) {

        String completeFileName = file.getOriginalFilename();
        String randomId = UUID.randomUUID().toString();
        assert completeFileName != null;
        String filename = randomId.concat(completeFileName.substring(completeFileName.lastIndexOf('.')));
        String filePath = path + File.separator + filename;

        File folder = new File(path);
        log.info("Absolute path is {}", folder.getAbsolutePath());

        if(!folder.exists())
            folder.mkdir();

        try {
            Files.copy(file.getInputStream(), Paths.get(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return filename;
    }


    public double calculateSpecialPrice(double price, double discount){
        double specialPrice = 0L;
        specialPrice = price - (discount * 0.01 * price);

        return specialPrice;
    }

    private ProductResponse getProductResponse(Integer pageNumber, Integer pageSize, Page<Product> pageProducts) {
        List<Product> products = pageProducts.getContent();
        List<ProductDto> productDtos = products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDtos);
        productResponse.setPageNumber(pageNumber);
        productResponse.setPageSize(pageSize);
        productResponse.setLastPage(pageProducts.isLast());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());

        return productResponse;
    }
}
