package com.example.mtg_deckbuilder.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

public class CustomUserDetails extends User {
    private final CustomUserDetails id; // Your database ID

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, CustomUserDetails id) {
        super(username, password, authorities);
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}