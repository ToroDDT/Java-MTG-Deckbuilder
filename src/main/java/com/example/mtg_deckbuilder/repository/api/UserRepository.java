package com.example.mtg_deckbuilder.repository.api;

import com.example.mtg_deckbuilder.dto.combo.UserRegistrationDto;
import com.example.mtg_deckbuilder.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    void saveUser(UserRegistrationDto dto);
}
