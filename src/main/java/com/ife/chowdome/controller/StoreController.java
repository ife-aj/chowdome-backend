package com.ife.chowdome.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ife.chowdome.dto.CreateStoreRequest;
import com.ife.chowdome.model.Food;
import com.ife.chowdome.model.Store;
import com.ife.chowdome.model.Users;
import com.ife.chowdome.repository.FoodRepo;
import com.ife.chowdome.repository.StoreRepo;
import com.ife.chowdome.repository.UserRepo;




@RestController
@RequestMapping("/api/store")
public class StoreController {
    @Autowired
    private StoreRepo storeRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FoodRepo foodRepo;


    @PostMapping("/add")
    public Object addStore(@RequestBody CreateStoreRequest store) {
       
        
        if(!storeRepo.findByStoreName(store.getStoreName()).isPresent() && !userRepo.findByEmail(store.getEmail()).isPresent()) {
            

            Users newUser = new Users();
            newUser.setFirstName(store.getFirstName());
            newUser.setLastName(store.getLastName());
            newUser.setEmail(store.getEmail());
            newUser.setPassword(store.getPassword());
            newUser.setPhoneNumber(store.getContactNumber());
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setRole("STORE"); // Default role for store owners

            // Save the user to the database
            Users savedUser = userRepo.save(newUser);

            Store newStore = new Store();
            newStore.setFirstName(store.getFirstName());
            newStore.setLastName(store.getLastName());
            newStore.setStoreName(store.getStoreName());
            newStore.setLocation(store.getLocation());
            newStore.setContactNumber(store.getContactNumber());
            newStore.setEmail(store.getEmail());
            newStore.setOpeningHours(store.getOpeningHours());
            newStore.setClosingHours(store.getClosingHours());
            newStore.setDescription(store.getDescription());
            newStore.setImageUrl(store.getImageUrl());
            newStore.setPassword(store.getPassword());
            newStore.setUser(savedUser);
            storeRepo.save(newStore);
        
            Map<String, Object> storeData = new HashMap<>();
            storeData.put("id", newStore.getId());
            storeData.put("firstName", newStore.getFirstName());
            storeData.put("lastName", newStore.getLastName());
            storeData.put("storeName", newStore.getStoreName());
            storeData.put("location", newStore.getLocation());
            storeData.put("contactNumber", newStore.getContactNumber());
            storeData.put("email", newStore.getEmail());
            storeData.put("openingHours", newStore.getOpeningHours());
            storeData.put("closingHours", newStore.getClosingHours());
            storeData.put("description", newStore.getDescription());
            storeData.put("imageUrl", newStore.getImageUrl()); // if needed

            return Map.of(
                    "error", false,
                    "message", "Store added successfully",
                    "data", storeData
            );

        } else {
            return Map.of(
                "error", true,
                "message", "Store with this name already exists or user with this email already exists"
            );
        }
        
    }
    
    @GetMapping("/{id}")
    public Object getStoreById(@PathVariable Long id) {
       if(storeRepo.findById(id).isPresent()){
            Store store = storeRepo.findById(id).get();
            Map<String, Object> storeData = new HashMap<>();
            storeData.put("id", store.getId());
            storeData.put("firstName", store.getFirstName());
            storeData.put("lastName", store.getLastName());
            storeData.put("storeName", store.getStoreName());
            storeData.put("location", store.getLocation());
            storeData.put("contactNumber", store.getContactNumber());
            storeData.put("email", store.getEmail());
            storeData.put("openingHours", store.getOpeningHours());
            storeData.put("closingHours", store.getClosingHours());
            storeData.put("description", store.getDescription());
            storeData.put("imageUrl", store.getImageUrl());

            return Map.of(
                "error", false,
                "message", "Store retrieved successfully",
                "data", storeData
            );
        }else{
            return Map.of(
                "error", true,
                "message", "Store not found"
            );
        }
    }
    
    @GetMapping("/{id}/food")
    public Object getStoreFoodById(@RequestParam Long id) {
        if(storeRepo.existsById(id)){
            List<Food> foods = foodRepo.findByStoreId(id);
            return Map.of(
                "error", false,
                "message", "Foods retrieved successfully",
                "data", foods
            );
        }else{
            return Map.of(
                "error", true,
                "message", "Store not found"
            );}
        }

    @GetMapping("/all")
    public Object getAllStores() {
        List<Store> stores = storeRepo.findAll();
        if (stores.isEmpty()) {
            return Map.of(
                "error", true,
                "message", "No stores found"
            );
        } else {
            List<Map<String, Object>> storeData = new ArrayList<>();
            for (Store s : stores) {
                Map<String, Object> storeMap = new HashMap<>();
                storeMap.put("id", s.getId());
                storeMap.put("firstName", s.getFirstName());
                storeMap.put("lastName", s.getLastName());
                storeMap.put("storeName", s.getStoreName());
                storeMap.put("location", s.getLocation());
                storeMap.put("contactNumber", s.getContactNumber());
                storeMap.put("email", s.getEmail());
                storeMap.put("openingHours", s.getOpeningHours());
                storeMap.put("closingHours", s.getClosingHours());
                storeMap.put("description", s.getDescription());
                storeMap.put("imageUrl", s.getImageUrl());

                storeData.add(storeMap);
            }

            return Map.of(
                "error", false,
                "message", "Store retrieved successfully",
                "data", storeData
            );
        }
    }

    @PutMapping("/update/{id}")
    public Object updateStore(@PathVariable Long id, @RequestBody CreateStoreRequest updatedData) {
        return storeRepo.findById(id)
            .map(store -> {
                if (updatedData.getFirstName() != null) {
                    store.setFirstName(updatedData.getFirstName());
                }
                if (updatedData.getLastName() != null) {
                    store.setLastName(updatedData.getLastName());
                }
                if (updatedData.getStoreName() != null) {
                    store.setStoreName(updatedData.getStoreName());
                }
                if (updatedData.getLocation() != null) {
                    store.setLocation(updatedData.getLocation());
                }
                if (updatedData.getContactNumber() != null) {
                    store.setContactNumber(updatedData.getContactNumber());
                }
                if (updatedData.getEmail() != null) {
                    store.setEmail(updatedData.getEmail());
                }
                if (updatedData.getOpeningHours() != null) {
                    store.setOpeningHours(updatedData.getOpeningHours());
                }
                if (updatedData.getClosingHours() != null) {
                    store.setClosingHours(updatedData.getClosingHours());
                }
                if (updatedData.getDescription() != null) {
                    store.setDescription(updatedData.getDescription());
                }
                if (updatedData.getImageUrl() != null) {
                    store.setImageUrl(updatedData.getImageUrl());
                }

                storeRepo.save(store);
            Map<String, Object> storeData = new HashMap<>();
            storeData.put("id", store.getId());
            storeData.put("firstName", store.getFirstName());
            storeData.put("lastName", store.getLastName());
            storeData.put("storeName", store.getStoreName());
            storeData.put("location", store.getLocation());
            storeData.put("contactNumber", store.getContactNumber());
            storeData.put("email", store.getEmail());
            storeData.put("openingHours", store.getOpeningHours());
            storeData.put("closingHours", store.getClosingHours());
            storeData.put("description", store.getDescription());
            storeData.put("imageUrl", store.getImageUrl()); // if needed
                
                return Map.of(
                    "error", false,
                    "message", "Store updated successfully",
                    "data", storeData
                );
            })
            .orElse(Map.of(
                "error", true,
                "message", "Store not found"
            ));
    }

    @DeleteMapping("/delete/{id}")
    public Object deleteStore(@PathVariable Long id) {
        if (storeRepo.existsById(id)) {
            userRepo.deleteByEmail(storeRepo.findById(id).get().getEmail());
            storeRepo.deleteById(id);
            
            return Map.of(
                "error", false,
                "message", "Store deleted successfully"
            );
        } else {
            return Map.of(
                "error", true,
                "message", "Store not found"
            );
        }
    }
        
        

}

