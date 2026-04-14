package com.example.mtg_deckbuilder.views;

import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.SortOptions;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder // This generates the Builder pattern for you
public class LibraryViewModel {
    private final List<String> allColors = List.of("W", "U", "B", "R", "G");
    private final List<OwnedCard> cards;
    private final Map<String, Long> colorIdentityAmounts;
    private final Double totalValue;
    private final Integer totalCards;
    private final Double avgPrice;
    private final SortOptions[] sortOptions = SortOptions.values();
    private final LibraryFilters filters = new LibraryFilters();
    private final List<String> deckNames;
}