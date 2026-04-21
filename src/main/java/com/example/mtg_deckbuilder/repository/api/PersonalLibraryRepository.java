package com.example.mtg_deckbuilder.repository.api;

import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.OwnedCard;

import java.util.List;
import java.util.UUID;

public interface PersonalLibraryRepository {
    List<OwnedCard> getAllPersonalLibraryCardsForUser(UUID userId, LibraryFilters personalLibraryFilters);
    void addCardToPersonalLibrary(OwnedCard ownedCard);
}
