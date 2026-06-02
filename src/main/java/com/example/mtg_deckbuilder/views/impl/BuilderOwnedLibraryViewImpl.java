package com.example.mtg_deckbuilder.views.impl;

import com.example.mtg_deckbuilder.views.api.BuilderOwnedLibraryView;
import com.example.mtg_deckbuilder.views.api.LibraryViewModel;

public record BuilderOwnedLibraryViewImpl(
        LibraryViewModel libraryView,
        String builderDeckId,
        String builderDeckName
) implements BuilderOwnedLibraryView {

    public static BuilderOwnedLibraryView of(LibraryViewModel libraryView,
                                             String builderDeckId,
                                             String builderDeckName) {
        return new BuilderOwnedLibraryViewImpl(libraryView, builderDeckId, builderDeckName);
    }
}
