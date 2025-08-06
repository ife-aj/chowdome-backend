package com.ife.chowdome.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ife.chowdome.model.CartItem;
import com.ife.chowdome.model.Users;

import jakarta.transaction.Transactional;

public interface CartItemRepo extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserId(Long userId);
    List<CartItem> findByUser(Users user);
    List<CartItem> findByFoodId(Long foodId);
    Optional<CartItem> findByUserIdAndFoodId(Long userId, Long foodId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem c WHERE c.user = :user")
    void deleteByUser(@Param("user") Users user);
}
