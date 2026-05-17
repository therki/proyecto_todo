package com.openwebinars.todo.repos;

import com.openwebinars.todo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findFirstByUsername(String username);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
}