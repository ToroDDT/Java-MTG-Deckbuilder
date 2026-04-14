package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.dto.UserRegistrationDto;
import com.example.mtg_deckbuilder.exceptions.InvalidRegistrationFormException;
import com.example.mtg_deckbuilder.model.User;
import com.example.mtg_deckbuilder.repository.api.UserRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public @NonNull UserDetails loadUserByUsername (@NonNull String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("username input was empty");
        }
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        return new CustomUserDetails(
                user.get().username(),
                user.get().password(),
                authorities,
                user.get().id()

        );
    }
    public void saveUser(@NotNull UserRegistrationDto userRegistrationDto) throws InvalidRegistrationFormException {
        if (userRegistrationDto.getUsername().isBlank() || userRegistrationDto.getPassword().isBlank() || userRegistrationDto.getEmail().isBlank()){
            throw new InvalidRegistrationFormException("Missing required fields, some form fields are empty.");
        }
        userRepository.saveUser(userRegistrationDto);
    }
}