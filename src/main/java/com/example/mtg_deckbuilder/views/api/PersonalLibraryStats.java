package com.example.mtg_deckbuilder.views.api;

import com.example.mtg_deckbuilder.model.ColorIdentity;

import java.util.Map;

public interface PersonalLibraryStats {

    Double totalValue();

    Map<ColorIdentity, Long> colorIdentityAmounts();

    Integer totalCards();

    Double avgPrice();
}
