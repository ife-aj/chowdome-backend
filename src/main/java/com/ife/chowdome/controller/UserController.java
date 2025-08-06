package com.ife.chowdome.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ife.chowdome.model.Users;
import com.ife.chowdome.repository.UserRepo;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepo userRepo;

    
  @GetMapping("/me")
  public Object getCurrentUser() {
    // This method should return the currently authenticated user's details.
    // For now, we can return a placeholder response.
    Users user = (Users) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    
    return Map.of(
        "error", false,
        "message", "User details fetched successfully",
        "data", Map.of(
            "id", user.getId(),
            "firstName", user.getFirstName(),
            "lastName", user.getLastName(),
            "email", user.getEmail(),
            "phoneNumber", user.getPhoneNumber(),
            "role", user.getRole()
        )
    );}

    @GetMapping("/all")
    public Object getAllUsers(){
        List<Users> users = userRepo.findAll();
        if(users.isEmpty()) {
            return Map.of(
                "error", true,
                "message", "No users found"
            );
        }else{
            List<Map<String, Object>> result = new ArrayList<>();
            for(Users user : users){
                Map<String, Object> userMap = Map.of(
                    "id", user.getId(),
                    "firstName", user.getFirstName(),
                    "lastName", user.getLastName(),
                    "email", user.getEmail(),
                    "phoneNumber", user.getPhoneNumber(),
                    "role", user.getRole(),
                    "createdAt", user.getCreatedAt()
                );
                result.add(userMap);
            }
            return Map.of(
                "error", false,
                "message", "Users fetched successfully",
                "data", result
            );
        }

    }

    @DeleteMapping("/delete/{id}")
    public Object deleteUserById(@PathVariable Long id) {
        if(userRepo.existsById(id)) {
            userRepo.deleteById(id);
            return Map.of(
                "error", false,
                "message", "User deleted successfully"
            );
        } else {
            return Map.of(
                "error", true,
                "message", "User not found"
            );
        }
    }

    
  
    

}
