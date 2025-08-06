package com.ife.chowdome.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ife.chowdome.model.Store;

public interface StoreRepo extends JpaRepository<Store, Long> {
    Optional<Store> findByStoreName(String storeName);
}