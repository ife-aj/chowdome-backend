package com.ife.chowdome.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ife.chowdome.model.CartItem;
import com.ife.chowdome.model.Users;
import com.ife.chowdome.repository.CartItemRepo;
import com.ife.chowdome.repository.UserRepo;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    @Autowired
    private CartItemRepo cartItemRepo;

    @Autowired
    private UserRepo userRepo;

    @PostMapping("/add")
    public Object addCartItem(@RequestBody CartItem cartItem) {
        if (cartItem.getFood() == null || cartItem.getUser() == null) {
            return Map.of("error", true, "message", "Food and User must not be null");
        }

        Optional<CartItem> existingCartItem = cartItemRepo.findByUserIdAndFoodId(cartItem.getUser().getId(), cartItem.getFood().getId());
        if (existingCartItem.isPresent()) {
            CartItem item = existingCartItem.get();
            item.setQuantity(item.getQuantity() + cartItem.getQuantity());
            item.setFood(cartItem.getFood());
            item.setUser(cartItem.getUser());
            cartItemRepo.save(item);
        } else {
            cartItemRepo.save(cartItem);
            
        }
        
        return Map.of("error", false, "message", "Cart item added successfully");
    }

    @PutMapping("/update/{id}")
    public Object updateCartItem(@RequestBody CartItem cartItem, @PathVariable Long id) {
        Optional<CartItem> existingCartItem = cartItemRepo.findById(id);
        if (existingCartItem.isPresent()) {
            CartItem item = existingCartItem.get();
            item.setQuantity(cartItem.getQuantity());
            cartItemRepo.save(item);
            
            Map<String, Object> simplified = Map.of(
                "id", item.getId(),
                "food", item.getFood().getName(),
                "quantity", item.getQuantity()
            );
            return Map.of(
                "error", false,
                "message", "Cart item updated successfully",
                "cartItem", simplified
            );
            
        } else {
            return Map.of("error", true, "message", "Cart item not found");
        }
    }

    @GetMapping
    public Object getUserCart(Authentication auth){
       Users users = (Users) auth.getPrincipal();
       String email = users.getEmail();
        Optional<Users> userOptional = userRepo.findByEmail(email);
        if(userOptional.isEmpty()){
            return Map.of(
                "error", true,
                "message", "User not found"
            );
        }
        Users user = userOptional.get();
        List<CartItem> cartItems = cartItemRepo.findByUserId(user.getId());
        if(cartItems.isEmpty()) {
            return Map.of(
                "error", true,
                "message", "No items in cart"
            );
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (CartItem item : cartItems) {
            Map<String, Object> simplified = Map.of(
                "id", item.getId(),
                "food", item.getFood().getName(),
                "quantity", item.getQuantity()
            );
            result.add(simplified);
        }
        return Map.of(
            "error", false,
            "message", "Cart items retrieved successfully",
            "cartItems", result
        );
        
    }

    @GetMapping("/all")
    public Object getAllCartItems() {
        List<CartItem> cartItems = cartItemRepo.findAll();
        if(cartItems.isEmpty()) {
            return Map.of("error", true, "message", "No cart items found");
        }else{
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (CartItem item : cartItems) {
            Map<String, Object> simplified = Map.of(
                "id", item.getId(),
                "food", item.getFood().getName(),
                "quantity", item.getQuantity(),
                "user", item.getUser().getEmail()
            );
            result.add(simplified);
        }
        
        return Map.of("error", false, "message", "Cart items retrieved successfully", "cartItems", result);}
    }

    @DeleteMapping("/delete/{id}")
    public Object deleteCartItem(@PathVariable Long id) {
        Optional<CartItem> existingCartItem = cartItemRepo.findById(id);
        if (existingCartItem.isPresent()) {
            cartItemRepo.delete(existingCartItem.get());
            return Map.of("error", false, "message", "Cart item deleted successfully");
        } else {
            return Map.of("error", true, "message", "Cart item not found");
        }
    }
    
}
