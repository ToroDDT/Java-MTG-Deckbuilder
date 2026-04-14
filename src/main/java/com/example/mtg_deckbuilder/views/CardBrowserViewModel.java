package com.example.mtg_deckbuilder.views;

import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.model.PersonalLibraryFilters;
import com.example.mtg_deckbuilder.model.SortOptions;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class CardBrowserViewModel{
    private final List<String> allColors = List.of("W", "U", "B", "R", "G");
    private final SortOptions[] sortOptions = SortOptions.values();
    private final PersonalLibraryFilters filters = new PersonalLibraryFilters();

    public CardBrowserViewModel() {
    }
}
