package com.example.mtg_deckbuilder.repository.api;

import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.views.PersonalLibraryStats;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PersonalLibraryRepository {
  List<OwnedCard> findCards(UUID userId, LibraryFilters personalLibraryFilters);

  Boolean findCardExists(UUID userId, String cardId);

  List<OwnedCard> findCards(UUID userId);

  List<OwnedCard> findCardsForCombos(UUID userId);


  List<OwnedCard> findCardsPaginated (UUID userId);

  void saveCard(OwnedCard ownedCard);

  Map<UUID, List<String>> findLocations(CustomUserDetails user, List<UUID> cardIds);

  List<OwnedCard>getInfo(CustomUserDetails user);

  List<String> saveTags (String tag, UUID personalCardId, CustomUserDetails user);

  List<String> deleteTag (String tag, UUID personalCardId, CustomUserDetails user);

  void deleteCard (CustomUserDetails user, UUID cardId);
}
