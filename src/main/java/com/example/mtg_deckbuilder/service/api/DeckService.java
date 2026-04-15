package com.example.mtg_deckbuilder.service.api;

import com.example.mtg_deckbuilder.model.DeckRequest;
import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.utils.DeckSearchCriteria;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DeckService {
    void addDeck(NewDeck newDeck);
    void addCard(DeckRequest card);
    Map<?, ?> getDecks(UUID user , DeckSearchCriteria deckSearchCriteria);
    List<String> getDeckNames(CustomUserDetails user);
}
