package com.example.mtg_deckbuilder.views.impl;

import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.SortOptions;
import com.example.mtg_deckbuilder.views.api.CardBrowserViewModel;
import lombok.Getter;

import java.util.List;

@Getter
public class CardBrowserViewModelImpl implements CardBrowserViewModel {
    private final List<String> allColors = List.of("W", "U", "B", "R", "G");
    private final SortOptions[] sortOptions = SortOptions.values();
    private final LibraryFilters filters = new LibraryFilters();

    public CardBrowserViewModelImpl() {
    }
}
