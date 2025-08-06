package com.ife.chowdome.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ife.chowdome.model.Order;
import com.ife.chowdome.model.Users;

public interface OrderRepo extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    Optional<Order> findByIdAndUserId(Long id, Long userId);
    

    public List<Order> findByUser(Users user);
}
