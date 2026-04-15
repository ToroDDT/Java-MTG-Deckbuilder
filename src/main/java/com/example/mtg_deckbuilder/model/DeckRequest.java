package com.example.mtg_deckbuilder.model;

import java.util.UUID;

public record DeckRequest (
        UUID deckId,
        UUID userId,
        UUID cardId,
        boolean isSideboard,
        UUID personalLibraryCardId
) {}
