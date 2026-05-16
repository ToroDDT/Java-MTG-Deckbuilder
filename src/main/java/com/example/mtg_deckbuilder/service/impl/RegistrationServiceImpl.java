package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.dto.combo.UserRegistrationDto;
import com.example.mtg_deckbuilder.exceptions.UserAlreadyExistsException;
import com.example.mtg_deckbuilder.model.User;
import com.example.mtg_deckbuilder.repository.api.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegistrationServiceImpl {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(
            @NotNull UserRegistrationDto dto
    ) throws UserAlreadyExistsException {


        if (userRepository.findByUsername(dto.getUsername()).isEmpty()) {
            throw new UserAlreadyExistsException(
                    "Username already in use"
            );
        }

        UserRegistrationDto userDto = new UserRegistrationDto();
        userDto.setEmail(dto.getEmail());
        userDto.setUsername(dto.getUsername());
        userDto.setPassword(passwordEncoder.encode(dto.getPassword()));
        User user = new User(
                null,
                dto.getUsername(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword())
        );

        userRepository.saveUser(userDto);
    }
}
