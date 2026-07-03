package com.example.mtg_deckbuilder.views.impl;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.model.SortOptions;
import com.example.mtg_deckbuilder.views.api.CardCatalogViewModel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class CardCatalogViewModelImpl implements CardCatalogViewModel {

    private final List<Card> cards;
    private final Map<String, Long> colorIdentityAmounts;
    private final Double totalValue;
    private final Integer totalCards;
    private final Double avgPrice;
    private final SortOptions[] sortOptions = SortOptions.values();
}
