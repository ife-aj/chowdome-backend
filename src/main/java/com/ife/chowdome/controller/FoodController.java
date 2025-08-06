package com.ife.chowdome.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ife.chowdome.dto.CreateFoodRequest;
import com.ife.chowdome.model.Food;
import com.ife.chowdome.model.Store;
import com.ife.chowdome.model.Users;
import com.ife.chowdome.repository.FoodRepo;
import com.ife.chowdome.repository.StoreRepo;


@RestController
@RequestMapping("/api/food")
public class FoodController {
    @Autowired
    private FoodRepo foodRepo;

    @Autowired
    private StoreRepo storeRepo;

    

    @PostMapping("/add")
    public Object addFood(@RequestBody CreateFoodRequest food) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) auth.getPrincipal();
        
        
        if(!foodRepo.findByName(food.getName()).isPresent()) {
            Store store = storeRepo.findById(food.getStoreId()).orElse(null);
            if (store == null) {
                return Map.of(
                    "error", true,
                    "message", "Store not found"
                );
            }
            if (!user.getEmail().equals(store.getEmail()) && !user.getRole().equals("ADMIN")) {
            return Map.of(
                    "error", true,
                    "message", "You can only add a food with your own email"
            );
        }
            // Create a new food item
            Food newFood = new Food();
            newFood.setName(food.getName());
            newFood.setDescription(food.getDescription());
            newFood.setPrice(food.getPrice());
            newFood.setAvailability(food.getAvailability());
            newFood.setCategory(food.getCategory());
            newFood.setImageUrl(food.getImageUrl());
            newFood.setDuration(food.getDuration());
            newFood.setStore(store);

            // Save the food item to the database
            foodRepo.save(newFood);
            
            return Map.of(
                "error", false,
                "message", "Food item added successfully",
                "data", Map.of(
                    "id", newFood.getId(),
                    "name", newFood.getName(),
                    "description", newFood.getDescription(),
                    "price", newFood.getPrice(),
                    "availability", newFood.getAvailability(),
                    "category", newFood.getCategory(),
                    "imageUrl", newFood.getImageUrl(),
                    "duration", newFood.getDuration(),
                    "storeName", store.getStoreName()
                )
            );
        } else {
            return Map.of(
                "error", true,
                "message", "Food item with this ID already exists"
            );
        }
    }

    @GetMapping("/category/{category}")
    public Object getFoodByCategory(@PathVariable String category) {
        List<Food> foods = foodRepo.findByCategoryIgnoreCase(category);
        if(!foods.isEmpty()){
        List<Map<String, Object>> result = new ArrayList<>();
        for (Food food : foods) {
            Map<String, Object> simplified = Map.ofEntries(
                    Map.entry("id", food.getId()),
                    Map.entry("name", food.getName()),
                    Map.entry("price", food.getPrice()),
                    Map.entry("availability", food.getAvailability()),
                    Map.entry("description", food.getDescription()),
                    Map.entry("category", food.getCategory()),
                    Map.entry("duration", food.getDuration()),
                    Map.entry("storeName", food.getStore().getStoreName()),
                    Map.entry("imageUrl", food.getImageUrl())
            );

            result.add(simplified);
        }

        return Map.of(
                "error", false,
                "data", result
        );}else{
            return Map.of(
                "error", true,
                "message", "No food found with the given category"
            );
        }
    }

    @GetMapping("/availability/{availability}")
    public Object getFoodByAvailability(@PathVariable String availability) {
        List<Food> foods = foodRepo.findByAvailabilityIgnoreCase(availability);
        if(!foods.isEmpty()){
        List<Map<String, Object>> result = new ArrayList<>();
        for (Food food : foods) {
            Map<String, Object> simplified = Map.ofEntries(
                    Map.entry("id", food.getId()),
                    Map.entry("name", food.getName()),
                    Map.entry("price", food.getPrice()),
                    Map.entry("availability", food.getAvailability()),
                    Map.entry("description", food.getDescription()),
                    Map.entry("category", food.getCategory()),
                    Map.entry("duration", food.getDuration()),
                    Map.entry("storeName", food.getStore().getStoreName()),
                    Map.entry("imageUrl", food.getImageUrl())
            );

            result.add(simplified);
        }

        return Map.of(
                "error", false,
                "data", result
        );}else {
            return Map.of(
                "error", true,
                "message", "No food found with the given availability"
            );
        }
    }

    @GetMapping("/store/{storeId}/categories")
    public Object getCategories(@PathVariable Long storeId) {
        List<String> categories = foodRepo.findDistinctCategoriesByStoreId(storeId);
        if(!categories.isEmpty()){
        return Map.of(
            "error", false,
            "message", "Categories retrieved successfully",
            "data", categories
        );}
        else {
            return Map.of(
                "error", true,
                "message", "No categories found"
            );
        }
    }

    @GetMapping("/durations")
    public Object getDurations() {
        List<String> durations = foodRepo.findDistinctDurations();
        if(!durations.isEmpty()){
        return Map.of(
            "error", false,
            "message", "Durations retrieved successfully",
            "data", durations
        );}
        else {
            return Map.of(
                "error", true,
                "message", "No durations found"
            );
        }
    }

    @GetMapping("/duration/{duration}")
    public Object getFoodByDuration(@PathVariable String duration) {
        List<Food> foods = foodRepo.findByDurationIgnoreCase(duration);
        if(!foods.isEmpty()){
        List<Map<String, Object>> result = new ArrayList<>();
        for (Food food : foods) {
            Map<String, Object> simplified = Map.ofEntries(
                    Map.entry("id", food.getId()),
                    Map.entry("name", food.getName()),
                    Map.entry("price", food.getPrice()),
                    Map.entry("availability", food.getAvailability()),
                    Map.entry("category", food.getCategory()),
                    Map.entry("duration", food.getDuration()),
                    Map.entry("storeName", food.getStore().getStoreName()),
                    Map.entry("imageUrl", food.getImageUrl())
            );

            result.add(simplified);
        }

        return Map.of(
                "error", false,
                "data", result
        );}
        else {
            return Map.of(
                "error", true,
                "message", "No food found with the given duration"
            );
        }
        
    }

    @GetMapping("/search")
    public Object searchFood(@RequestParam String keyword){
        List<Food> foods = foodRepo.searchByKeyword(keyword);
        if(!foods.isEmpty()){
        List<Map<String, Object>> result = new ArrayList<>();
        for (Food food : foods) {
            Map<String, Object> simplified = Map.ofEntries(
                    Map.entry("id", food.getId()),
                    Map.entry("name", food.getName()),
                    Map.entry("price", food.getPrice()),
                    Map.entry("availability", food.getAvailability()),
                    Map.entry("description", food.getDescription()),
                    Map.entry("category", food.getCategory()),
                    Map.entry("duration", food.getDuration()),
                    Map.entry("storeName", food.getStore().getStoreName()),
                    Map.entry("imageUrl", food.getImageUrl())
            );

           result.add(simplified);
        }

        return Map.of(
                "error", false,
                "data", result
        );}
        else {
            return Map.of(
                "error", true,
                "message", "No food found with the given keyword"
            );
        }
        
        
    }

   

    @GetMapping("/store/{storeId}")
    public Object getFoodsByStore(@PathVariable Long storeId) {
        List<Food> foods = foodRepo.findByStoreId(storeId);
        Store store = storeRepo.findById(storeId).orElse(null);

        if (store == null) {
            return Map.of(
                    "error", true,
                    "message", "Store not found"
            );
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (Food food : foods) {
            Map<String, Object> simplified = Map.ofEntries(
                    Map.entry("id", food.getId()),
                    Map.entry("name", food.getName()),
                    Map.entry("price", food.getPrice()),
                    Map.entry("description", food.getDescription()),
                    Map.entry("availability", food.getAvailability()),
                    Map.entry("category", food.getCategory()),
                    Map.entry("duration", food.getDuration()),
                    Map.entry("storeName", food.getStore().getStoreName()),
                    Map.entry("imageUrl", food.getImageUrl())
            );

            result.add(simplified);
        }

        return Map.of(
                "error", false,
                "data", result
        );
    }


    
    @PutMapping("/update/{id}")
    public Object updateFood(@PathVariable Long id, @RequestBody Food updatedData) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) auth.getPrincipal();
        
        

        if (foodRepo.findById(id).isPresent()) {
            Food food = foodRepo.findById(id).get();
           
        Store store = storeRepo.findById(food.getStore().getId()).orElse(null);
            if (store == null) {
                return Map.of(
                    "error", true,
                    "message", "Store not found"
                );
            }
            if (!user.getEmail().equals(store.getEmail()) && !user.getRole().equals("ADMIN")) {
            return Map.of(
                    "error", true,
                    "message", "You are not allowed to update food from this store"
            );
        }

            // Update only non-null values
            if (updatedData.getName() != null) {
                food.setName(updatedData.getName());
            }
            if (updatedData.getDescription() != null) {
                food.setDescription(updatedData.getDescription());
            }
            if (updatedData.getPrice() != 0) {
                food.setPrice(updatedData.getPrice());
            }
            if (updatedData.getAvailability() != null) {
                food.setAvailability(updatedData.getAvailability());
            }
            if (updatedData.getCategory() != null) {
                food.setCategory(updatedData.getCategory());
            }
            if (updatedData.getImageUrl() != null) {
                food.setImageUrl(updatedData.getImageUrl());
            }
            if (updatedData.getDuration() != null) {
                food.setDuration(updatedData.getDuration());
            }

            foodRepo.save(food);

            Map<String, Object> simplified = Map.ofEntries(
        Map.entry("id", food.getId()),
        Map.entry("name", food.getName()),
        Map.entry("price", food.getPrice()),
        Map.entry("availability", food.getAvailability()),
        Map.entry("description", food.getDescription()),
        Map.entry("category", food.getCategory()),
        Map.entry("duration", food.getDuration()),
        Map.entry("storeName", food.getStore().getStoreName()),
        Map.entry("imageUrl", food.getImageUrl())
    );

    return Map.of("error", false, "data", simplified);
        }

        return Map.of("error", true, "message", "Food not found");
    }

    @DeleteMapping("/delete/{id}")
    public Object deleteFood(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Users user = (Users) auth.getPrincipal();
        if (foodRepo.existsById(id)) {
            Food food = foodRepo.findById(id).get();
            Store store = storeRepo.findById(food.getStore().getId()).orElse(null);
            if (store == null) {
                return Map.of(
                        "error", true,
                        "message", "Store not found"
                );
            }
            if (!user.getEmail().equals(store.getEmail()) && !user.getRole().equals("ADMIN")) {
                return Map.of(
                        "error", true,
                        "message", "You are not allowed to delete food from this store"
                );
            }
            foodRepo.deleteById(id);
            return Map.of(
                    "error", false,
                    "message", "Food deleted successfully"
            );
        } else {
            return Map.of(
                    "error", true,
                    "message", "Food not found"
            );
        }
    }

    

    @GetMapping("/all")
    public Object getAllFoods() {
        List<Food> foods = foodRepo.findAll();
        if (foods.isEmpty()) {
            return Map.of(
                "error", true,
                "message", "No foods found"
            );
        }else {
            List<Map<String, Object>> simplified = foods.stream().map(food -> Map.<String, Object>ofEntries(
        Map.entry("id", food.getId()),
        Map.entry("name", food.getName()),
        Map.entry("price", food.getPrice()),
        Map.entry("availability", food.getAvailability()),
        Map.entry("category", food.getCategory()),
        Map.entry("description", food.getDescription()),
        Map.entry("duration", food.getDuration()),
        Map.entry("storeName", food.getStore().getStoreName()),
        Map.entry("imageUrl", food.getImageUrl())
    )).collect(Collectors.toList());

    return Map.of("error", false, "data", simplified);
        }
    }

    @GetMapping("/{id}")
    public Object getFoodById(@PathVariable Long id) {
        if (foodRepo.findById(id).isPresent()) {
        Food food = foodRepo.findById(id).get();
        Map<String, Object> simplified = Map.ofEntries(
        Map.entry("id", food.getId()),
        Map.entry("name", food.getName()),
        Map.entry("price", food.getPrice()),
        Map.entry("availability", food.getAvailability()),
        Map.entry("description", food.getDescription()),
        Map.entry("category", food.getCategory()),
        Map.entry("duration", food.getDuration()),
        Map.entry("storeName", food.getStore().getStoreName()),
        Map.entry("imageUrl", food.getImageUrl())
    );

    return Map.of("error", false, "data", simplified);
        } else {
            return Map.of(
                "error", true,
                "message", "Food not found"
            );
        }
    }


    
    
    
        
        

}
