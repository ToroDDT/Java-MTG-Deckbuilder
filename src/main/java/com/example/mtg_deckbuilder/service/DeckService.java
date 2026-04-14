package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.model.AddCardToDeckRequest;
import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.utils.DeckSearchCriteria;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DeckService {
    void addDeck(NewDeck newDeck);
    void addCardToDeck(AddCardToDeckRequest card);
    Map<?, ?> getAllDecksForUser(UUID user , DeckSearchCriteria deckSearchCriteria);
    List<String> getALlDeckNames(CustomUserDetails user);
}
