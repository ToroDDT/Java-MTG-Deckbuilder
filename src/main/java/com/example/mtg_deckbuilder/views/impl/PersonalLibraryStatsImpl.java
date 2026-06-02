package com.example.mtg_deckbuilder.views.impl;

import com.example.mtg_deckbuilder.model.ColorIdentity;
import com.example.mtg_deckbuilder.views.api.PersonalLibraryStats;
import lombok.Builder;

import java.util.Map;

@Builder
public record PersonalLibraryStatsImpl(
        Double totalValue,
        Map<ColorIdentity, Long> colorIdentityAmounts,
        Integer totalCards,
        Double avgPrice
) implements PersonalLibraryStats {
}
