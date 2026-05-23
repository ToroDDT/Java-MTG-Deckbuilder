package com.example.mtg_deckbuilder.views;


import lombok.Builder;

import java.util.List;

@Builder
public record BuilderViewModel(
        String image,
        Double totalValue,
        String deckName,
        List<BuilderDeckCardRecord> creatures,
        List<Long> manaCurveData,
        List<BuilderDeckCardRecord> instants,
        List<BuilderDeckCardRecord> enchantments,
        List<BuilderDeckCardRecord> artifacts,
        List<BuilderDeckCardRecord> lands,
        List<BuilderDeckCardRecord> sorceries,
        List<Long> colorProduction,
        String deckId,
        List<String> colors
) {
}
