package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.SortOptions;
import com.example.mtg_deckbuilder.service.api.CardBrowserService;
import com.example.mtg_deckbuilder.service.api.CardService;
import com.example.mtg_deckbuilder.views.api.CardCatalogViewModel;
import com.example.mtg_deckbuilder.views.impl.CardCatalogViewModelImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CardBrowserServiceImpl implements CardBrowserService {

    private final CardService cardService;

    public CardBrowserServiceImpl(CardService cardService) {
        this.cardService = cardService;
    }

    @Override
    public CardCatalogViewModel buildViewModel() {
        return buildViewModel(defaultFilters());
    }

    @Override
    public CardCatalogViewModel buildViewModel(LibraryFilters filters) {
        LibraryFilters activeFilters = filters != null ? filters : defaultFilters();
        List<Card> cards = cardService.findCards(activeFilters);

        double totalValue = cards.stream()
                .filter(card -> card.getPrices() != null && card.getPrices().getUsd() != null)
                .mapToDouble(card -> card.getPrices().getUsd())
                .sum();

        int totalCards = cards.size();
        double avgPrice = totalCards == 0 ? 0.0 : totalValue / totalCards;

        return CardCatalogViewModelImpl.builder()
                .cards(cards)
                .colorIdentityAmounts(colorCounts(cards))
                .totalValue(totalValue)
                .totalCards(totalCards)
                .avgPrice(avgPrice)
                .build();
    }

    private static Map<String, Long> colorCounts(List<Card> cards) {
        Map<String, Long> counts = new HashMap<>();
        for (Card card : cards) {
            List<String> colors = card.getColorIdentity();
            if (colors == null || colors.isEmpty()) {
                counts.merge("COLORLESS", 1L, Long::sum);
                continue;
            }
            for (String color : colors) {
                switch (color) {
                    case "W" -> counts.merge("WHITE", 1L, Long::sum);
                    case "U" -> counts.merge("BLUE", 1L, Long::sum);
                    case "B" -> counts.merge("BLACK", 1L, Long::sum);
                    case "R" -> counts.merge("RED", 1L, Long::sum);
                    case "G" -> counts.merge("GREEN", 1L, Long::sum);
                    default -> { }
                }
            }
        }
        return counts;
    }

    private static LibraryFilters defaultFilters() {
        LibraryFilters filters = new LibraryFilters();
        filters.setSortBy(SortOptions.RECENT);
        filters.setMinCMC(0);
        filters.setMaxCMC(16);
        filters.setPage(0);
        return filters;
    }
}
