package com.example.mtg_deckbuilder.service.api;

import com.example.mtg_deckbuilder.model.ColorIdentity;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.views.LibraryViewModel;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PersonalLibraryService {
    void addCard(OwnedCard ownedCard, UUID userId);
    List<OwnedCard> getCards(UUID userId);
    List<OwnedCard> getCards(UUID userId, LibraryFilters personalLibraryFilters);
    Map<ColorIdentity, Long> getAmountOfEachColorIdentity(UUID userId);
    LibraryViewModel buildPersonalLibraryViewModel(CustomUserDetails user);
    LibraryViewModel buildPersonalLibraryViewModel(CustomUserDetails user, LibraryFilters personalLibraryFilters);

}
