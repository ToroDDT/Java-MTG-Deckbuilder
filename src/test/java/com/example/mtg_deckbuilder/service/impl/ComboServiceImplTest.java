package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.dto.card.Prices;
import com.example.mtg_deckbuilder.dto.combo.CardCombos;
import com.example.mtg_deckbuilder.dto.combo.CardDto;
import com.example.mtg_deckbuilder.dto.combo.CardUse;
import com.example.mtg_deckbuilder.dto.combo.ComboItem;
import com.example.mtg_deckbuilder.dto.combo.ComboVariant;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.views.api.ComboDetailViewModel;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComboServiceImplTest {
    @Test
    void findStoredComboDetailUsesPersistedVariantWithoutApiLookup() {
        ComboVariant variant = comboVariant(
        );
        CardCombos stored = CardCombos.builder()
                .items(List.of(ComboItem.builder()
                        .cardCombination(List.of("Goblin Bombardment", "Gravecrawler"))
                        .description("Deal infinite damage.")
                        .location("library")
                        .variant(variant)
                        .build()))
                .build();
   }

    private static List<List<String>> comboNames(CardCombos combos) {
        return combos.getItems().stream()
                .map(ComboItem::getCardCombination)
                .toList();
    }

    private static ComboVariant comboVariant() {
        ComboVariant variant = new ComboVariant();
        variant.description = "Deal infinite damage.";
        variant.uses = java.util.Arrays.stream(new String[]{"Goblin Bombardment", "Gravecrawler"})
                .map(name -> {
                    CardUse cardUse = new CardUse();
                    CardDto card = new CardDto();
                    card.name = name;
                    cardUse.card = card;
                    return cardUse;
                })
                .toList();
        return variant;
    }

    private static CardCombos combos() {
        return CardCombos.builder()
                .items(List.of(
                        ComboItem.builder()
                                .cardCombination(List.of("Goblin Bombardment", "Gravecrawler"))
                                .description("Deal infinite damage.")
                                .images(List.of("goblin.jpg", "gravecrawler.jpg"))
                                .location("library")
                                .result("Infinite damage.")
                                .build(),
                        ComboItem.builder()
                                .cardCombination(List.of("Phyrexian Altar", "Pitiless Plunderer"))
                                .description("Create infinite treasure tokens.")
                                .images(List.of("altar.jpg", "plunderer.jpg"))
                                .location("Artifacts Deck")
                                .result("Infinite treasure tokens.")
                                .build()
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
