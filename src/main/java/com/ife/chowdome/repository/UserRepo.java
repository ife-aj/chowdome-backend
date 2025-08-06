package com.ife.chowdome.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ife.chowdome.model.Users;

public interface UserRepo extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    public void deleteByEmail(String email);
    
    

    
}
