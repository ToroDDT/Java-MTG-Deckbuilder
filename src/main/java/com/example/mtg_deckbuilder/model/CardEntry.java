package com.example.mtg_deckbuilder.model;

import com.example.mtg_deckbuilder.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CardEntry(
        UUID deckId,
        CustomUserDetails userId,
        UUID cardId,
        boolean isSideboard,
        UUID personalLibraryCardId,
        Boolean owned
) {}
