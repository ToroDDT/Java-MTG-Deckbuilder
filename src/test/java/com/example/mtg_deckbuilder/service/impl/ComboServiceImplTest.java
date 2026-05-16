package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.dto.card.Prices;
import com.example.mtg_deckbuilder.dto.combo.CardCombos;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.SortOptions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComboServiceImplTest {

    @Test
    void filterCombosMatchesCardNameAndDescription() {
        CardCombos combos = combos();
        LibraryFilters filters = new LibraryFilters();
        filters.setCardName("damage");

        CardCombos filtered = ComboServiceImpl.filterCombos(combos, filters, cardMetadata());

        assertEquals(List.of(List.of("Goblin Bombardment", "Gravecrawler")), filtered.getCardCombinations());
    }

    @Test
    void filterCombosMatchesMultiWordSearchAcrossEntireCombo() {
        CardCombos combos = combos();
        LibraryFilters filters = new LibraryFilters();
        filters.setCardName("goblin gravecrawler");

        CardCombos filtered = ComboServiceImpl.filterCombos(combos, filters, cardMetadata());

        assertEquals(List.of(List.of("Goblin Bombardment", "Gravecrawler")), filtered.getCardCombinations());
    }

    @Test
    void filterCombosMatchesSelectedColorsFromComboCards() {
        CardCombos combos = combos();
        LibraryFilters filters = new LibraryFilters();
        filters.setSelectedColors(List.of("B", "R"));

        CardCombos filtered = ComboServiceImpl.filterCombos(combos, filters, cardMetadata());

        assertEquals(List.of(List.of("Goblin Bombardment", "Gravecrawler")), filtered.getCardCombinations());
    }

    @Test
    void filterCombosMatchesCardTypeAndCmc() {
        CardCombos combos = combos();
        LibraryFilters filters = new LibraryFilters();
        filters.setCardType("Artifact");
        filters.setMinCMC(3);
        filters.setMaxCMC(7);

        CardCombos filtered = ComboServiceImpl.filterCombos(combos, filters, cardMetadata());

        assertEquals(List.of(List.of("Phyrexian Altar", "Pitiless Plunderer")), filtered.getCardCombinations());
    }

    @Test
    void filterCombosAppliesMaxCmcToComboTotal() {
        CardCombos combos = combos();
        LibraryFilters filters = new LibraryFilters();
        filters.setMaxCMC(3);

        CardCombos filtered = ComboServiceImpl.filterCombos(combos, filters, cardMetadata());

        assertEquals(List.of(List.of("Goblin Bombardment", "Gravecrawler")), filtered.getCardCombinations());
    }

    @Test
    void filterCombosMatchesPriceRangeAgainstAnyCardInCombo() {
        CardCombos combos = combos();
        LibraryFilters filters = new LibraryFilters();
        filters.setMinPrice(4.50);
        filters.setMaxPrice(5.50);

        CardCombos filtered = ComboServiceImpl.filterCombos(combos, filters, cardMetadata());

        assertEquals(List.of(List.of("Goblin Bombardment", "Gravecrawler")), filtered.getCardCombinations());
    }

    @Test
    void filterCombosMatchesLocation() {
        CardCombos combos = combos();
        LibraryFilters filters = new LibraryFilters();
        filters.setLocation("library");

        CardCombos filtered = ComboServiceImpl.filterCombos(combos, filters, cardMetadata());

        assertEquals(List.of(List.of("Goblin Bombardment", "Gravecrawler")), filtered.getCardCombinations());
    }

    @Test
    void filterCombosSortsByTotalPrice() {
        CardCombos combos = combos();
        LibraryFilters filters = new LibraryFilters();
        filters.setSortBy(SortOptions.PRICE_DESC);

        CardCombos filtered = ComboServiceImpl.filterCombos(combos, filters, cardMetadata());

        assertEquals(List.of(
                List.of("Phyrexian Altar", "Pitiless Plunderer"),
                List.of("Goblin Bombardment", "Gravecrawler")
        ), filtered.getCardCombinations());
    }

    private static CardCombos combos() {
        return CardCombos.builder()
                .cardCombinations(List.of(
                        List.of("Goblin Bombardment", "Gravecrawler"),
                        List.of("Phyrexian Altar", "Pitiless Plunderer")
                ))
                .description(List.of(
                        "Deal infinite damage.",
                        "Create infinite treasure tokens."
                ))
                .images(List.of(
                        List.of("goblin.jpg", "gravecrawler.jpg"),
                        List.of("altar.jpg", "plunderer.jpg")
                ))
                .locations(List.of("library", "Artifacts Deck"))
                .results(List.of(
                        "Infinite damage.",
                        "Infinite treasure tokens."
                ))
                .build();
    }

    private static Map<String, Card> cardMetadata() {
        return Map.of(
                "goblin bombardment", card("Goblin Bombardment", "Enchantment", 2, 5.00, List.of("R")),
                "gravecrawler", card("Gravecrawler", "Creature - Zombie", 1, 1.25, List.of("B")),
                "phyrexian altar", card("Phyrexian Altar", "Artifact", 3, 40.00, List.of()),
                "pitiless plunderer", card("Pitiless Plunderer", "Creature - Human Pirate", 4, 10.00, List.of("B"))
        );
    }

    private static Card card(String name, String typeLine, int cmc, double usdPrice, List<String> colorIdentity) {
        return Card.builder()
                .name(name)
                .typeLine(typeLine)
                .cmc(cmc)
                .colorIdentity(colorIdentity)
                .prices(Prices.builder().usd(usdPrice).build())
                .build();
    }
}
