package com.example.mtg_deckbuilder.views;

import lombok.Builder;

import java.util.List;

@Builder
public record BuilderDeckLayoutView(
        BuilderViewModel builderView,
        String deckViewStyle,
        DeckLayoutExtrasFlags deckExtras,
        List<BuilderDeckSection> deckSections,
        boolean deckListCondensed,
        boolean deckVisualSplit,
        boolean deckSpoilerReveal
) {
    public static BuilderDeckLayoutView of(BuilderViewModel builderView,
                                           String deckViewStyle,
                                           DeckLayoutExtrasFlags deckExtras,
                                           List<BuilderDeckSection> deckSections) {
        return BuilderDeckLayoutView.builder()
                .builderView(builderView)
                .deckViewStyle(deckViewStyle)
                .deckExtras(deckExtras)
                .deckSections(deckSections)
                .deckListCondensed("condensed".equals(deckViewStyle))
                .deckVisualSplit("visual-split".equals(deckViewStyle))
                .deckSpoilerReveal("visual-spoiler".equals(deckViewStyle))
                .build();
    }
}
