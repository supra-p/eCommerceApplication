package com.ecommerce.controllers;

import com.ecommerce.dto.CartDto;
import com.ecommerce.services.CartService;
import com.ecommerce.utils.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartController {

    private final CartService cartService;
    private final AuthUtil authUtil;

    public CartController(CartService cartService, AuthUtil authUtil) {
        this.cartService = cartService;
        this.authUtil = authUtil;
    }

    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDto> addProductToCart(
            @PathVariable Long productId, @PathVariable Integer quantity
    ){
        CartDto cartDto = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<>(cartDto, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDto>> getAllCarts(){
        List<CartDto> cartDtos = cartService.getAllCarts();
        return  ResponseEntity.ok(cartDtos);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDto> getCardByID(){
        String emailId = authUtil.loggedInEmail();
        CartDto cart = cartService.getCart(emailId);
        return  ResponseEntity.ok(cart);
    }

    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDto> updateCartProduct(
            @PathVariable Long productId,
            @PathVariable String operation
    ) {
        CartDto cartDto = cartService.updateProductQuantityInCart(productId,
                operation.equalsIgnoreCase("delete")? -1 : 1);
        return ResponseEntity.ok(cartDto);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(
            @PathVariable Long productId,
            @PathVariable Long cartId
    ){
        String emailId = authUtil.loggedInEmail();
        String message = cartService.deleteProductFromCart(cartId, productId);
        return  ResponseEntity.ok(message);
    }


}
