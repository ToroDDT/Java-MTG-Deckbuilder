package com.example.mtg_deckbuilder.service.api;

import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.views.BuilderCardHoverView;
import com.example.mtg_deckbuilder.views.BuilderCardQueryView;
import com.example.mtg_deckbuilder.views.BuilderDeckLayoutView;
import com.example.mtg_deckbuilder.views.BuilderDeckSection;
import com.example.mtg_deckbuilder.views.BuilderMainView;
import com.example.mtg_deckbuilder.views.BuilderOwnedLibraryView;
import com.example.mtg_deckbuilder.views.BuilderViewModel;
import com.example.mtg_deckbuilder.security.CustomUserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BuilderService {

    BuilderViewModel getBuilderView(String deckId);

    BuilderMainView getMainView(String deckId, CustomUserDetails user);

    BuilderDeckLayoutView getDeckLayoutView(String deckId, String viewStyle, String groupBy, String sortBy, List<String> extrasParams);

    List<BuilderDeckSection> buildDeckSections(String deckId, String groupBy, String sortBy);

    BuilderCardQueryView getCardQueryView(String query);

    BuilderOwnedLibraryView getOwnedLibraryView(String deckId, CustomUserDetails user);

    Optional<BuilderCardHoverView> getDeckEntryHover(CustomUserDetails user, UUID deckId, UUID deckCardEntryId);

    List<OwnedCard> getCardsFromDeck(UUID deckId);

    List<String> getRandomizedCards(UUID deckId);

    void removeDeckEntry(CustomUserDetails user, UUID deckId, UUID deckCardEntryId);

    void addCardToDeck(CustomUserDetails user, String deckId, String cardName);

    String optimizeDecksAgainstOpponent();

    String optimizeDeck();
}
