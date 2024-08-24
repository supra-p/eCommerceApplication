package com.ecommerce.services;

import com.ecommerce.dto.OrderDto;
import com.ecommerce.dto.OrderItemDto;
import com.ecommerce.exceptions.resourceNotFoundException;
import com.ecommerce.models.*;
import com.ecommerce.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    private final CartRepository cartRepo;
    private final AddressRepository addressRepo;
    private final PaymentRepository paymentRepo;
    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final CartService cartService;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepo;

    public OrderServiceImpl(CartRepository cartRepo, AddressRepository addressRepo, PaymentRepository paymentRepo, OrderRepository orderRepo, OrderItemRepository orderItemRepo, CartService cartService, ModelMapper modelMapper, ProductRepository productRepo) {
        this.cartRepo = cartRepo;
        this.addressRepo = addressRepo;
        this.paymentRepo = paymentRepo;
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
        this.productRepo = productRepo;
    }

    @Override
    @Transactional
    public OrderDto placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {

            Cart cart = cartRepo.findCartByEmail(emailId);
            if(cart == null)
                throw new resourceNotFoundException("Cart not found");

            Address address = addressRepo.findById(addressId)
                    .orElseThrow(()->new resourceNotFoundException("Address not found"));

            Order order = new Order();
            order.setEmail(emailId);
            order.setAddress(address);
            order.setOrderStatus("Order Accepted!");
            order.setTotalAmount(cart.getTotalPrice());
            order.setOrderDate(LocalDate.now());

            Payment payment = new Payment(paymentMethod, pgPaymentId, pgStatus, pgResponseMessage, pgName);
            payment.setOrder(order);
            paymentRepo.save(payment);
            order.setPayment(payment);

            Order savedOrder = orderRepo.save(order);
            List<CartItem> cartItemList = cart.getCartItem();
            if(cartItemList.isEmpty())
                throw new resourceNotFoundException("Cart is empty");

            List<OrderItem> orderItems = new ArrayList<>();
            for(CartItem cartItem : cartItemList){
                OrderItem orderItem = new OrderItem();
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setDiscount(cartItem.getDiscount());
                orderItem.setProduct(cartItem.getProduct());
                orderItem.setOrderedProductPrice(cartItem.getProductPrice());
                orderItem.setOrder(savedOrder);

                orderItems.add(orderItem);
            }

            orderItems = orderItemRepo.saveAll(orderItems);

            cart.getCartItem().forEach(item -> {
                int quantity = item.getQuantity();
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() - quantity);
                productRepo.save(product);

                cartService.deleteProductFromCart(cart.getCartId(), product.getProductId());
            });

            OrderDto orderDto = modelMapper.map(order, OrderDto.class);
            orderItems.forEach(item -> orderDto.getOrderItems().add(modelMapper.map(item, OrderItemDto.class)));
            orderDto.setAddressId(addressId);

            return orderDto;
    }
}
