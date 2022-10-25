package com.ll.exam.Week_Mission.app.cart.repository;

import com.ll.exam.Week_Mission.app.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByBuyerIdAndProductId(Long buyerId, Long productId);

    boolean existsByBuyerIdAndProductId(Long buyerId, Long productId);

    List<CartItem> findAllByBuyerId(Long buyerId);
}
