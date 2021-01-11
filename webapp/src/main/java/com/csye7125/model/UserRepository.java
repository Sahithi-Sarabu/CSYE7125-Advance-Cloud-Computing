package com.csye7125.model;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User,String> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsById(String id);
    User findUserByUsername(String username);
}
