package com.example.mtg_deckbuilder.views;

import com.example.mtg_deckbuilder.model.ColorIdentity;

import java.util.Map;

public record PersonalLibraryStats(
        Double totalValue,
        Map<ColorIdentity, Long> colorIdentityAmounts,
        Integer totalCards,
        Double avgPrice
) {}

