package com.example.mtg_deckbuilder.service.api;

import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.views.BuilderCardHoverView;
import com.example.mtg_deckbuilder.views.BuilderViewModel;
import com.example.mtg_deckbuilder.security.CustomUserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BuilderService {

    BuilderViewModel getBuilderView(String deckId);

    Optional<BuilderCardHoverView> getDeckEntryHover(CustomUserDetails user, UUID deckId, UUID deckCardEntryId);

    List<OwnedCard> getCardsFromDeck(UUID deckId);

    String optimizeDecksAgainstOpponent();

    String optimizeDeck();
}
