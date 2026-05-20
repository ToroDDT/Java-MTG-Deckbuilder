package com.example.mtg_deckbuilder.repository.api;

import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.views.BuilderCardHoverView;
import com.example.mtg_deckbuilder.views.BuilderDeckCardRecord;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BuilderRepository {
    List<BuilderDeckCardRecord> getAllCardsForUser(String deckId);
    List<OwnedCard> getAllCardsFromDeck(UUID deckId) ;
    Optional<BuilderCardHoverView> findDeckEntryHover(UUID userId, UUID deckId, UUID deckCardEntryId);
}
