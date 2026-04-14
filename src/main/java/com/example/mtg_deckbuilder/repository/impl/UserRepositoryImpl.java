package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.dto.UserRegistrationDto;
import com.example.mtg_deckbuilder.model.User;
import com.example.mtg_deckbuilder.repository.api.UserRepository;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final JdbcClient jdbcClient;
    private final PasswordEncoder passwordEncoder;

    public UserRepositoryImpl(JdbcClient jdbcClient, PasswordEncoder passwordEncoder) {
        this.jdbcClient = jdbcClient;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public Optional<User> findByUsername(String username) {
        return jdbcClient.sql("SELECT * FROM \"users\" WHERE username = :username")
                .param("username", username)
                .query(User.class)
                .optional();
    }
    @Override
    public void saveUser(UserRegistrationDto dto) {
    jdbcClient.sql(" INSERT INTO users (id, username, email, password) VALUES (:id, :username, :email, :password)")
        .param("id", UUID.randomUUID()) // Generates the UUID automatically
        .param("username", dto.getUsername())
        .param("email", dto.getEmail())
        .param("password", passwordEncoder.encode(dto.getPassword())) // Hashes the password
        .update(); // Use .update() for INSERT/UPDATE/DELETE
    }
}
