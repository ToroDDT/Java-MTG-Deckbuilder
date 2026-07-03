package com.example.mtg_deckbuilder.views.api;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.model.SortOptions;

import java.util.List;
import java.util.Map;

public interface CardCatalogViewModel {

    List<Card> getCards();

    Map<String, Long> getColorIdentityAmounts();

    Double getTotalValue();

    Integer getTotalCards();

    Double getAvgPrice();

    SortOptions[] getSortOptions();
}
