package com.example.mtg_deckbuilder.views;

import com.example.mtg_deckbuilder.model.ColorIdentity;
import lombok.Builder;

import java.util.Map;

@Builder
public record PersonalLibraryStats(
        Double totalValue,
        Map<ColorIdentity, Long> colorIdentityAmounts,
        Integer totalCards,
        Double avgPrice
) {}

