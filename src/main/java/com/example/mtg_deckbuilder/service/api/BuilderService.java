package com.example.mtg_deckbuilder.service.api;

import com.example.mtg_deckbuilder.views.BuilderCardHoverView;
import com.example.mtg_deckbuilder.views.BuilderViewModel;
import com.example.mtg_deckbuilder.security.CustomUserDetails;

import java.util.Optional;
import java.util.UUID;

public interface BuilderService {
    BuilderViewModel getBuilderView(String deckId);

    Optional<BuilderCardHoverView> getDeckEntryHover(CustomUserDetails user, UUID deckId, UUID deckCardEntryId);

    String optimizeDecksAgainstOpponent();

    String optimizeDeck();
}
