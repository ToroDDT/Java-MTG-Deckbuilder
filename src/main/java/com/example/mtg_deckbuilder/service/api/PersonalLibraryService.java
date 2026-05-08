package com.example.mtg_deckbuilder.service.api;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.model.ColorIdentity;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.views.LibraryViewModelImpl;
import com.example.mtg_deckbuilder.views.PersonalLibraryStats;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PersonalLibraryService {
  void addCard(OwnedCard ownedCard, UUID userId);

  List<OwnedCard> getCards(UUID userId);

  List<OwnedCard> getCards(UUID userId, LibraryFilters personalLibraryFilters);

  Map<ColorIdentity, Long> getAmountOfEachColorIdentity(UUID userId);

  LibraryViewModelImpl buildPersonalLibraryViewModel(CustomUserDetails user);

  LibraryViewModelImpl buildPersonalLibraryViewModel(CustomUserDetails user, LibraryFilters personalLibraryFilters);

  Map<UUID, List<String>> getDeckLocationsOfCards(CustomUserDetails user);

  PersonalLibraryStats getStatsOfPersonalLibrary(CustomUserDetails user);

  List<Card> getCardQuery(String query);

  List<String> updateCardTags(String tag, String personalCardId, CustomUserDetails user);

  List<String> removeCardTag(String tag, String personalCardId, CustomUserDetails user);

  void delete(CustomUserDetails user, String cardId);
}
