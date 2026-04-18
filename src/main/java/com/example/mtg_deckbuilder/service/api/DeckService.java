package com.example.mtg_deckbuilder.service.api;

import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.CardEntry;
import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.utils.DeckSearchCriteria;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DeckService {
    void addDeck(NewDeck newDeck);
    void addCard(CardEntry card);
    String addCard(CustomUserDetails user, String deck, UUID cardId, UUID personalLibraryCardId);
    List<Deck> getDecks(CustomUserDetails user);
    Map<?, ?> getDecks(CustomUserDetails user , DeckSearchCriteria deckSearchCriteria);
    List<String> getDeckNames(CustomUserDetails user);
}
