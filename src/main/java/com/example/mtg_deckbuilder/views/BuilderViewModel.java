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
    private static final List<Long> EMPTY_MANA_CURVE =
            List.of(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
    private static final List<Long> EMPTY_COLOR_PRODUCTION =
            List.of(0L, 0L, 0L, 0L, 0L, 0L);

    public static BuilderViewModel empty(String deckId) {
        return BuilderViewModel.builder()
                .image("")
                .deckId(deckId)
                .manaCurveData(EMPTY_MANA_CURVE)
                .lands(List.of())
                .artifacts(List.of())
                .creatures(List.of())
                .colorProduction(EMPTY_COLOR_PRODUCTION)
                .enchantments(List.of())
                .colors(List.of())
                .sorceries(List.of())
                .totalValue(0.0)
                .deckName("")
                .build();
    }

    public static BuilderViewModel of(String deckId,
                                      String deckName,
                                      String image,
                                      Double totalValue,
                                      List<BuilderDeckCardRecord> creatures,
                                      List<Long> manaCurveData,
                                      List<BuilderDeckCardRecord> instants,
                                      List<BuilderDeckCardRecord> enchantments,
                                      List<BuilderDeckCardRecord> artifacts,
                                      List<BuilderDeckCardRecord> lands,
                                      List<BuilderDeckCardRecord> sorceries,
                                      List<Long> colorProduction,
                                      List<String> colors) {
        return BuilderViewModel.builder()
                .image(image)
                .totalValue(totalValue)
                .deckName(deckName)
                .creatures(creatures)
                .manaCurveData(manaCurveData)
                .instants(instants)
                .colors(colors)
                .enchantments(enchantments)
                .artifacts(artifacts)
                .lands(lands)
                .colorProduction(colorProduction)
                .sorceries(sorceries)
                .deckId(deckId)
                .build();
    }
}
