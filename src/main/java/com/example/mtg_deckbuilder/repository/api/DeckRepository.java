package com.example.mtg_deckbuilder.repository.api;

import com.example.mtg_deckbuilder.model.CardEntry;
import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.security.CustomUserDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DeckRepository {
    void createNewDeckEntry(NewDeck newDeck);

    List<Deck> getDecks(CustomUserDetails user);

    List<Deck> getDeckIds (CustomUserDetails user);

    Map<UUID, Double> getDeckTotalPricesForUser(UUID userId);

    void updateDeckMetadata(
            CustomUserDetails user,
            UUID deckId,
            String name,
            String commander,
            String colorIdentity,
            String image,
            LocalDate lastUpdate);

    void addCard(CardEntry request);

    void removeDeckEntryByPersonalLibraryCardId(CustomUserDetails user, UUID personalLibraryCardId);

    void removeDeckEntry(CustomUserDetails user, UUID deckId, UUID deckEntryId);
}
