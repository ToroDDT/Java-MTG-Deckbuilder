package com.example.mtg_deckbuilder.repository.api;

import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.security.CustomUserDetails;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PersonalLibraryRepository {
    List<OwnedCard> getAllPersonalLibraryCardsForUser(UUID userId, LibraryFilters personalLibraryFilters);
    List<OwnedCard> getAllPersonalLibraryCardsForUser(UUID userId);
    void addCardToPersonalLibrary(OwnedCard ownedCard);
    Map<UUID, List<String>> getDeckLocationsOfCards (CustomUserDetails user, List<UUID> cardIds);
}
