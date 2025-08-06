package com.ife.chowdome.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ife.chowdome.model.Food;

public interface FoodRepo extends JpaRepository<Food, Long> {
    Optional<Food> findByName(String name);
    List<Food> findByStoreId(Long storeId);
    List<Food> findByCategoryIgnoreCase(String category);
    List<Food> findByAvailabilityIgnoreCase(String availability);
    List<Food> findByDurationIgnoreCase(String duration);
    @Query("SELECT DISTINCT f.category FROM Food f")
    List<String> findDistinctCategories();

    @Query("SELECT DISTINCT f.category FROM Food f WHERE f.store.id = :storeId")
    List<String> findDistinctCategoriesByStoreId(@Param("storeId") Long storeId);


    @Query("SELECT DISTINCT f.duration FROM Food f")
    List<String> findDistinctDurations();

    @Query("SELECT f FROM Food f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Food> searchByKeyword(@Param("keyword") String keyword);

    




}