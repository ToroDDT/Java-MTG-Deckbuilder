package com.example.mtg_deckbuilder.repository.api;

import com.example.mtg_deckbuilder.model.DeckRequest;
import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.security.CustomUserDetails;

import java.util.List;
import java.util.UUID;

public interface DeckRepository {
    void createNewDeckEntry(NewDeck newDeck);

    List<Deck> getDecks(UUID userId);

    List<String> getDeckNames(CustomUserDetails user);

    void addCard(DeckRequest request);
}
