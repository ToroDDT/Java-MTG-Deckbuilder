package com.example.mtg_deckbuilder.repository.api;

import com.example.mtg_deckbuilder.model.CardEntry;
import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.security.CustomUserDetails;

import java.util.List;
import java.util.UUID;

public interface DeckRepository {
    void createNewDeckEntry(NewDeck newDeck);

    List<Deck> getDecks(CustomUserDetails user);

    List<String> getDeckNames(CustomUserDetails user);

    void addCard(CardEntry request);

    void removeDeckEntryByPersonalLibraryCardId(CustomUserDetails user, UUID personalLibraryCardId);
}
