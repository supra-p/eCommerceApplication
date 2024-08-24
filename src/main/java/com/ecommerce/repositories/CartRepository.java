package com.ecommerce.repositories;

import com.ecommerce.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("Select ci from Cart ci where ci.user.email=?1")
    Cart findCartByEmail(String email);

    @Query("Select c from Cart c join fetch c.cartItem ci join fetch ci.product p where p.productId = ?1")
    List<Cart> findCartsByProductId(Long productId);
}
