package com.example.mtg_deckbuilder.views;

import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.SortOptions;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ComboViewModel {
    private final List<String> allColors = List.of("W", "U", "B", "R", "G");
    private final SortOptions[] sortOptions = SortOptions.values();
    private final LibraryFilters filters = new LibraryFilters();
    public ComboViewModel() {
    }
}
