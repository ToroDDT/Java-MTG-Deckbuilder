package com.example.mtg_deckbuilder.views.api;

import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.SortOptions;

import java.util.List;

public interface ComboViewModel {

    List<String> getAllColors();

    SortOptions[] getSortOptions();

    LibraryFilters getFilters();
}
