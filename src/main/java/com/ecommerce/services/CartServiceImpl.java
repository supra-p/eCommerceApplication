package com.ecommerce.services;

import com.ecommerce.dto.CartDto;
import com.ecommerce.dto.ProductDto;
import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.resourceNotFoundException;
import com.ecommerce.models.Cart;
import com.ecommerce.models.CartItem;
import com.ecommerce.models.Product;
import com.ecommerce.repositories.CartItemRepository;
import com.ecommerce.repositories.CartRepository;
import com.ecommerce.repositories.ProductRepository;
import com.ecommerce.utils.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepo;
    private final AuthUtil authUtil;
    private final ProductRepository productRepo;
    private final CartItemRepository cartItemRepo;
    private final ModelMapper modelMapper;

    public CartServiceImpl(CartRepository cartRepo, AuthUtil authUtil, ProductRepository productRepo, CartItemRepository cartItemRepo, ModelMapper modelMapper) {
        this.cartRepo = cartRepo;
        this.authUtil = authUtil;
        this.productRepo = productRepo;
        this.cartItemRepo = cartItemRepo;
        this.modelMapper = modelMapper;
    }


    @Override
    public CartDto addProductToCart(Long productId, Integer quantity) {
        Cart cart = createCart();
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new resourceNotFoundException("Product with ID " + productId + " not found!"));

        CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(productId, cart.getCartId());
        if(cartItem != null)
            throw new APIException("Product "+ product.getProductName() +" already exists");

        if(product.getQuantity() == 0)
            throw new APIException(product.getProductName()+" is not available.");

        if(product.getQuantity()<quantity)
            throw new APIException("Please, make an order of quantity less than or equal to "+product.getQuantity());

        CartItem cartItem1 = new CartItem();

        cartItem1.setProduct(product);
        cartItem1.setCart(cart);
        cartItem1.setQuantity(quantity);
        cartItem1.setDiscount(product.getDiscount());
        cartItem1.setProductPrice(product.getSpecialPrice());

        cartItemRepo.save(cartItem1);
        cart.setTotalPrice((quantity * product.getSpecialPrice()) + cart.getTotalPrice());
        cartRepo.save(cart);

        return getCartDto(cart);
    }

    @Override
    public List<CartDto> getAllCarts() {

        List<Cart> carts = cartRepo.findAll();
        if(carts.isEmpty())
                throw new resourceNotFoundException("No Cart Exist");

        return carts.stream()
                .map(cart -> {
                    CartDto cartDto = modelMapper.map(cart, CartDto.class);
                    List<ProductDto> productDtos = cart.getCartItem().stream()
                            .map(p ->{
                                ProductDto productDto = modelMapper.map(p.getProduct(), ProductDto.class);
                                productDto.setQuantity(p.getQuantity());
                                return productDto;
                            }).toList();

                    cartDto.setProducts(productDtos);
                    return cartDto;
                }).toList();
    }

    @Override
    public CartDto getCart(String emailId) {
        Cart cart = cartRepo.findCartByEmail(emailId);
        if(cart == null)
            throw new resourceNotFoundException("User cart not found!");
        cart.getCartItem().forEach(item -> item.getProduct().setQuantity(item.getQuantity()));
        List<ProductDto> productDtos = cart.getCartItem().stream()
                .map(p -> modelMapper.map(p.getProduct(), ProductDto.class)).toList();
        CartDto cartDto = modelMapper.map(cart, CartDto.class);
        cartDto.setProducts(productDtos);
        return cartDto;
    }

    @Transactional
    @Override
    public CartDto updateProductQuantityInCart(Long productId, Integer quantity) {

        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepo.findCartByEmail(emailId);
        if(userCart == null)
            throw new resourceNotFoundException("Cart not found!");

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new resourceNotFoundException("Product with ID " + productId + " not found!"));

        if(product.getQuantity() == 0)
            throw new APIException(product.getProductName()+" is not available.");

        if(product.getQuantity()<quantity)
            throw new APIException("Please, make an order of quantity less than or equal to "+product.getQuantity());

        CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(productId, userCart.getCartId());

        int newQuantity = cartItem.getQuantity() + quantity;
        if(newQuantity < -1)
            throw new resourceNotFoundException("Quantity cannot be negative");

        if(newQuantity == 0)
            deleteProductFromCart(userCart.getCartId(), productId);
        else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            userCart.setTotalPrice(userCart.getTotalPrice() + (quantity * product.getSpecialPrice()));
            cartRepo.save(userCart);
        }

        CartItem updatedCartItem = cartItemRepo.save(cartItem);
        if(updatedCartItem.getQuantity() == 0)
            cartItemRepo.deleteById(updatedCartItem.getCartItemId());

        return getCartDto(userCart);
    }

    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new resourceNotFoundException("Cart not found!"));

        CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(productId, cartId);
        if(cartItem == null)
            throw new resourceNotFoundException("Product does not exist in the cart");

        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));
        cartItemRepo.deleteCartItemByProductIdAndCartId(cartId, productId);

        return "Product %s has been removed successfully".formatted(cartItem.getProduct().getProductName());
    }

    @Override
    public void updateProductInCarts(Long cartId, Long productId) {

        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new resourceNotFoundException("Cart not found!"));

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new resourceNotFoundException("Product with ID " + productId + " not found!"));

        CartItem cartItem = cartItemRepo.findCartItemByProductIdAndCartId(productId, cartId);

        if(cartItem == null)
            throw new resourceNotFoundException("Product does not exist in the cart");

        double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());
        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice + (cartItem.getQuantity() * cartItem.getProductPrice()));
        cartItemRepo.save(cartItem);
    }


    // --------------------------------------HELPERS--------------------------------------
    private Cart createCart(){
        Cart userCart = cartRepo.findCartByEmail(authUtil.loggedInEmail());
        if(userCart!=null)
            return userCart;

        Cart cart = new Cart();
        cart.setTotalPrice(0.0);
        cart.setUser(authUtil.loggedInUser());

        return cartRepo.save(cart);
    }

    private CartDto getCartDto(Cart userCart) {
        CartDto cartDto = modelMapper.map(userCart, CartDto.class);

        cartDto.setProducts(userCart.getCartItem().stream()
                .map(i -> {
                    ProductDto productDto = modelMapper.map(i, ProductDto.class);
                    productDto.setQuantity(i.getQuantity());
                    return productDto;
                }).toList());

        return cartDto;
    }

}
