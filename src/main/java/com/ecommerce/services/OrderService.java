package com.ecommerce.services;

import com.ecommerce.dto.OrderDto;

public interface OrderService {
    OrderDto placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage);
}
