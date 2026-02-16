package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.dto.UserRegistrationDto;
import com.example.mtg_deckbuilder.exceptions.InvalidRegistrationFormException;
import com.example.mtg_deckbuilder.exceptions.InvalidUsernameException;
import com.example.mtg_deckbuilder.model.User;
import com.example.mtg_deckbuilder.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByUsername( String username) throws InvalidUsernameException {
        if (username == null || username.isEmpty()) {
            throw new InvalidUsernameException("Username can not be null or empty");
        }
        return userRepository.findByUsername(username);
    }
    public void saveUser(@NotNull UserRegistrationDto userRegistrationDto) throws InvalidRegistrationFormException {
        if (userRegistrationDto.getUsername().isBlank() || userRegistrationDto.getPassword().isBlank() || userRegistrationDto.getEmail().isBlank()){
            throw new InvalidRegistrationFormException("Missing required fields, some form fields are empty.");
        }
        userRepository.saveUser(userRegistrationDto);
    }
}
