package com.ecommerce.controllers;

import com.ecommerce.dto.OrderDto;
import com.ecommerce.dto.OrderRequestDto;
import com.ecommerce.services.OrderService;
import com.ecommerce.utils.AuthUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;
    private final AuthUtil authUtil;

    public OrderController(OrderService orderService, AuthUtil authUtil) {
        this.orderService = orderService;
        this.authUtil = authUtil;
    }


    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDto> orderProducts(@PathVariable String paymentMethod, @RequestBody OrderRequestDto orderRequestDto) {
        String emailId = authUtil.loggedInEmail();
        OrderDto order = orderService.placeOrder(
                emailId,
                orderRequestDto.getAddressId(),
                paymentMethod,
                orderRequestDto.getPgName(),
                orderRequestDto.getPgPaymentId(),
                orderRequestDto.getPgStatus(),
                orderRequestDto.getPgResponseMessage()
        );
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
}
