package com.example.mtg_deckbuilder.repository.api;

import com.example.mtg_deckbuilder.model.AddCardToDeckRequest;
import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.security.CustomUserDetails;

import java.util.List;
import java.util.UUID;

public interface DeckRepository {
    void createNewDeckEntry(NewDeck newDeck);

    List<Deck> getAllDecksForUser(UUID userId);

    List<String> getAllDeckNames(CustomUserDetails user);

    void addCardToDeck(AddCardToDeckRequest request);
}
