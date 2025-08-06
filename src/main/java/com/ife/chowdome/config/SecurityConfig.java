package com.ife.chowdome.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/**")
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                "/api/auth/**", 
                "/api/store/all", 
                "/api/food/search",
                "/api/food/store/{storeId}",
                "/api/food/category/{category}",
                "/api/food/store/{storeId}/categories",
                "/api/food/all"
                ).permitAll()
                
                .requestMatchers(
                        
                        
                        "/api/store/add",
                        "/api/store/update/**",
                        "/api/store/delete/**"
                        
                ).hasAuthority("ROLE_ADMIN")
                .requestMatchers(                     
                        "api/food/add",
                        "/api/food/update/**",
                        "/api/food/delete/**"
                ).hasAnyAuthority("ROLE_ADMIN", "ROLE_STORE")
                .requestMatchers(
                        "/api/cart/add",
                        "/api/cart/update/**",
                        "/api/cart/delete/**"
                ).hasAuthority("ROLE_USER")
                .requestMatchers(
                    "/api/cart/all",
                    "/api/users/all"
                    
                ).hasAuthority("ROLE_ADMIN")

                .requestMatchers(
                        "/api/order/place",
                        "/api/order/history",
                        "/status/{id}",
                        "/api/order/{id}/reorder"
                ).hasAuthority("ROLE_USER")
                .requestMatchers(
                        "/api/order/store-orders",
                        "/api/order/{id}/status"
                ).hasAnyAuthority("ROLE_STORE", "ROLE_ADMIN")
                .requestMatchers(
                         // GET all orders
                        "/api/order/admin/**" // Suggested route for admin-specific order view
                ).hasAuthority("ROLE_ADMIN")


                .anyRequest().authenticated()
                
            )
            
            .cors(cors -> {})
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
            

        return http.build();
    }
    
}
