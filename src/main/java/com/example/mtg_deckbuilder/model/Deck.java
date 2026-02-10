package com.example.mtg_deckbuilder.model;

import java.util.UUID;

public record Deck(
        UUID id,
        UUID deck_format_id,
        String description,
        String date
) {}
