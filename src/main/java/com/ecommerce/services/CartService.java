package com.ecommerce.services;

import com.ecommerce.dto.CartDto;

import java.util.List;


public interface CartService {
    CartDto addProductToCart(Long productId, Integer quantity);

    List<CartDto> getAllCarts();

    CartDto getCart(String emailId);

    CartDto updateProductQuantityInCart(Long productId, Integer delete);

    String deleteProductFromCart(Long cartId, Long productId);

    void updateProductInCarts(Long cartId, Long productId);
}
