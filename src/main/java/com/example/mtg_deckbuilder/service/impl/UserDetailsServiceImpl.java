package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.dto.combo.UserRegistrationDto;
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

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public @NonNull UserDetails loadUserByUsername(
            @NonNull String username
    ) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"
                        )
                );

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER")
        );

        return new CustomUserDetails(
                user.username(),
                user.password(),
                authorities,
                user.id()
        );
    }
}