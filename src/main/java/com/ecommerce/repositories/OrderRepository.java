package com.ecommerce.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.models.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}