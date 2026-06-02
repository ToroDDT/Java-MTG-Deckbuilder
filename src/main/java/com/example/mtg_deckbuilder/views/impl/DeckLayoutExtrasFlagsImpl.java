package com.example.mtg_deckbuilder.views.impl;

import com.example.mtg_deckbuilder.views.api.DeckLayoutExtrasFlags;
import lombok.Builder;

@Builder
public record DeckLayoutExtrasFlagsImpl(boolean showManaCost, boolean showPrice, boolean showSetSymbol)
        implements DeckLayoutExtrasFlags {

    public static DeckLayoutExtrasFlagsImpl from(java.util.Set<String> extras) {
        java.util.Set<String> requestedExtras = extras == null ? java.util.Set.of() : extras;
        return DeckLayoutExtrasFlagsImpl.builder()
                .showManaCost(requestedExtras.contains("mana-cost"))
                .showPrice(requestedExtras.contains("price"))
                .showSetSymbol(requestedExtras.contains("set-symbol"))
                .build();
    }
}
