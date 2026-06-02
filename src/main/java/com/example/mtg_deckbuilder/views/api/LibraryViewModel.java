package com.example.mtg_deckbuilder.views.api;

import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.model.SortOptions;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface LibraryViewModel {

    List<String> getAllColors();

    List<OwnedCard> getCards();

    Map<String, Long> getColorIdentityAmounts();

    Double getTotalValue();

    Integer getTotalCards();

    Double getAvgPrice();

    SortOptions[] getSortOptions();

    LibraryFilters getFilters();

    List<String> getDeckNames();

    LocalDate getDateAdded();
}
