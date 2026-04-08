package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.model.ColorIdentity;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.model.PersonalLibraryFilters;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.views.LibraryViewModel;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PersonalLibraryService {
    void addCardToPersonalLibrary(OwnedCard ownedCard, UUID userId);
    List<OwnedCard> getCardsFromPersonalLibrary(UUID userId);
    List<OwnedCard> getCardsFromPersonalLibrary(UUID userId, PersonalLibraryFilters personalLibraryFilters);
    Map<ColorIdentity, Long> getAmountOfEachColorIdentity(UUID userId);
    LibraryViewModel buildPersonalLibraryViewModel(CustomUserDetails user);
}
