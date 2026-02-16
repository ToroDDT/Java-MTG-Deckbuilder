package com.example.mtg_deckbuilder.model;

import java.util.UUID;

public record User(
        UUID id,
        String email,
        String username,
        String password,
        String created_at
) {}
