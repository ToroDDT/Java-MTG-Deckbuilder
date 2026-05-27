package com.example.mtg_deckbuilder.views;

import java.util.List;

public record BuilderDeckLayoutView(
        BuilderViewModel builderView,
        String deckViewStyle,
        DeckLayoutExtrasFlags deckExtras,
        List<BuilderDeckSection> deckSections,
        boolean deckListCondensed,
        boolean deckVisualSplit,
        boolean deckSpoilerReveal
) {
}
