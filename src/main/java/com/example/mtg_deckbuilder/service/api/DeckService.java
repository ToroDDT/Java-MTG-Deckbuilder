package com.example.mtg_deckbuilder.service.api;

import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.CardEntry;
import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.utils.DeckSearchCriteria;
import com.example.mtg_deckbuilder.views.api.DeckListItemView;

import java.util.List;
import java.util.UUID;

public interface DeckService {
    void addDeck(NewDeck newDeck);

    void addCard(CardEntry card);

    String addCard(CustomUserDetails user, String deck, UUID cardId, UUID personalLibraryCardId);
    void addCard(CustomUserDetails user, String deck, String cardId);

    List<DeckListItemView> getDecks(CustomUserDetails user , DeckSearchCriteria deckSearchCriteria);

    void updateDeck(CustomUserDetails user, UUID deckId, String name, String commander);

    List<Deck> getDeckIds(CustomUserDetails user);

    void removePersonalLibraryCardFromDeck(CustomUserDetails user, UUID personalLibraryCardId);

    void removeDeckEntry(CustomUserDetails user, UUID deckId, UUID deckEntryId);
}
