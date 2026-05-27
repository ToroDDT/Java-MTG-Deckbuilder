package com.example.mtg_deckbuilder.views;

public record BuilderOwnedLibraryView(
        LibraryViewModelImpl libraryView,
        String builderDeckId,
        String builderDeckName
) {
}
