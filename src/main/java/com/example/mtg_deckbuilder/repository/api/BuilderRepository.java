package com.example.mtg_deckbuilder.repository.api;

import com.example.mtg_deckbuilder.views.BuilderCardHoverView;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface BuilderRepository {
    List<Map<String, String>> getAllCardsForUser(String deckId);

    /** Deck entry scoped to owning user — for hover previews. */
    Optional<BuilderCardHoverView> findDeckEntryHover(UUID userId, UUID deckId, UUID deckCardEntryId);
}
