package com.ife.chowdome.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ife.chowdome.dto.LoginRequest;
import com.ife.chowdome.dto.RegisterRequest;
import com.ife.chowdome.model.Users;
import com.ife.chowdome.repository.UserRepo;
import com.ife.chowdome.services.JWTService;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private JWTService jwtService;
    


    @PostMapping("/register")
    public Object register(@RequestBody RegisterRequest request) {
        if(!userRepo.findByEmail(request.getEmail()).isPresent()) {
           Users newUser = new Users();
            newUser.setFirstName(request.getFirstName());
            newUser.setLastName(request.getLastName());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(request.getPassword());
            newUser.setPhoneNumber(request.getPhoneNumber());
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setRole("USER"); // Default role

            // Save the user to the database
            userRepo.save(newUser);
            return Map.of(
                "error", false,
                "message", "User created successfully",
                "data", Map.of(
                        "id", newUser.getId(),
                        "firstName", newUser.getFirstName(),
                        "lastName", newUser.getLastName(),
                        "phoneNumber", newUser.getPhoneNumber(),
                        "email", newUser.getEmail()
                        ));
        }else {
            return Map.of(
                "error", true,
                "message", "User with this email already exists"
            );
        }
        
        
        
        
    }

    @PostMapping("/login")
public Object login(@RequestBody LoginRequest request) {
    return userRepo.findByEmail(request.getEmail())
            .map(user -> {
                if (request.getPassword().equals(user.getPassword())) {
                    String token = jwtService.generateToken(user);
                    

                    // Build user data map safely
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("id", user.getId());
                    userData.put("firstName", user.getFirstName());
                    userData.put("lastName", user.getLastName());
                    userData.put("phoneNumber", user.getPhoneNumber());
                    userData.put("email", user.getEmail());
                    userData.put("role", user.getRole());

                    // Build full response map
                    Map<String, Object> response = new HashMap<>();
                    response.put("error", false);
                    response.put("message", "Login successful");
                    response.put("token", token);
                    response.put("data", userData);

                    return response;
                } else {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("error", true);
                    errorResponse.put("message", "Login failed - Incorrect password or email");
                    errorResponse.put("data", null);
                    return errorResponse;
                }
            })
            .orElseGet(() -> {
                Map<String, Object> notFoundResponse = new HashMap<>();
                notFoundResponse.put("error", true);
                notFoundResponse.put("message", "User not found");
                notFoundResponse.put("data", null);
                return notFoundResponse;
            });
}


    
   @GetMapping
public String testLoginRoute() {
    return "Login route accessible";
}


}
