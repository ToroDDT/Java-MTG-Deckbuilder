package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.model.User;
import com.example.mtg_deckbuilder.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public  MyUserDetailsService (UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("username input was empty");
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(user.get().username())
                .password(user.get().password()) // Should be BCrypt hashed
                .authorities("USER")
                .build();
    }
}