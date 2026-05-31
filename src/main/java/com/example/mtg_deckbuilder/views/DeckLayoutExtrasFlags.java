package com.example.mtg_deckbuilder.views;

import lombok.Builder;

import java.util.Set;

@Builder
public record DeckLayoutExtrasFlags(boolean showManaCost, boolean showPrice, boolean showSetSymbol) {
    public static DeckLayoutExtrasFlags from(Set<String> extras) {
        Set<String> requestedExtras = extras == null ? Set.of() : extras;
        return DeckLayoutExtrasFlags.builder()
                .showManaCost(requestedExtras.contains("mana-cost"))
                .showPrice(requestedExtras.contains("price"))
                .showSetSymbol(requestedExtras.contains("set-symbol"))
                .build();
    }
}
