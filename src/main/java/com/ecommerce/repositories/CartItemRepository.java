package com.ecommerce.repositories;

import com.ecommerce.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("Select ci from CartItem ci where ci.product.productId=?1 and ci.cart.cartId = ?2")
    CartItem findCartItemByProductIdAndCartId(Long productId, Long cartId);

    @Modifying
    @Query("delete from CartItem ci where ci.product.productId=?2 and ci.cart.cartId = ?1")
    void deleteCartItemByProductIdAndCartId(Long cartId, Long productId);
}
