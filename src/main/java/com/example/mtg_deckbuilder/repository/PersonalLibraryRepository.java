package com.example.mtg_deckbuilder.repository;

import com.example.mtg_deckbuilder.model.OwnedCard;

import java.util.List;
import java.util.UUID;

public interface PersonalLibraryRepository {
    List<OwnedCard> getAllPersonalLibraryCardsForUser(UUID userId);
    void addCardToPersonalLibrary(OwnedCard ownedCard);
}
