package com.ife.chowdome.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ife.chowdome.model.OrderItem;
import com.ife.chowdome.model.Users;

public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findByFoodId(Long foodId);
    List<OrderItem> findByFood_Store(Users storeUser);
    Optional<OrderItem> findByIdAndOrderId(Long id, Long orderId);
}
