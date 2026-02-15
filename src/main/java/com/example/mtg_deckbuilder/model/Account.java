package com.example.mtg_deckbuilder.model;

import java.util.UUID;

public record Account(
        UUID id,
        String first_name,
        String Last_name,
        String email,
        String username,
        String password,
        String account_created
) {}
