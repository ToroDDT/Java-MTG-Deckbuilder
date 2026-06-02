package com.example.mtg_deckbuilder.views.api;

import java.util.List;

public interface BuilderDeckLayoutView {

    BuilderViewModel builderView();

    String deckViewStyle();

    DeckLayoutExtrasFlags deckExtras();

    List<BuilderDeckSection> deckSections();

    boolean deckListCondensed();

    boolean deckVisualSplit();

    boolean deckSpoilerReveal();
}
