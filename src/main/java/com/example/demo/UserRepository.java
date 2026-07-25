package com.example.demo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<user, Long> {

user findByUsernameAndPassword(
        String username,
        String password);

user findByUsername(String username);
user findByEmail(String email);

}
