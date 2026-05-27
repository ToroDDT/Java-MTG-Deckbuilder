package com.example.mtg_deckbuilder.views;

public record BuilderOwnedLibraryView(
        LibraryViewModelImpl libraryView,
        String builderDeckId,
        String builderDeckName
) {
    public static BuilderOwnedLibraryView of(LibraryViewModelImpl libraryView,
                                             String builderDeckId,
                                             String builderDeckName) {
        return new BuilderOwnedLibraryView(libraryView, builderDeckId, builderDeckName);
    }
}
