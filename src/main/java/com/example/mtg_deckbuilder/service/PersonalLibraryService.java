package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.model.PersonalLibraryFilters;

import java.util.List;
import java.util.UUID;

public interface PersonalLibraryService {
    void addCardToPersonalLibrary(OwnedCard ownedCard, UUID userId);
    List<OwnedCard> getCardsFromPersonalLibrary(UUID userId);
    List<OwnedCard> getCardsFromPersonalLibrary(UUID userId, PersonalLibraryFilters personalLibraryFilters);
}
