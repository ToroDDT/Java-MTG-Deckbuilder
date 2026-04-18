package com.example.mtg_deckbuilder.model;

import com.example.mtg_deckbuilder.security.CustomUserDetails;

import java.util.UUID;

public record CardEntry(
        UUID deckId,
        CustomUserDetails userId,
        UUID cardId,
        boolean isSideboard,
        UUID personalLibraryCardId
) {}
