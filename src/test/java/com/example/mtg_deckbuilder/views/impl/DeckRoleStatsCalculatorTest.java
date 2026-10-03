package com.example.mtg_deckbuilder.views.impl;

import com.example.mtg_deckbuilder.dto.card.Card;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeckRoleStatsCalculatorTest {

    @Test
    void countsRampDrawRemovalAndWipesFromOracleText() {
        var solRing = Card.builder()
                .name("Sol Ring")
                .typeLine("Artifact")
                .oracleText("{T}: Add {C}{C}.")
                .build();
        var rampGrowth = Card.builder()
                .name("Rampant Growth")
                .typeLine("Sorcery")
                .oracleText("Search your library for a basic land card, put that card onto the battlefield, then shuffle.")
                .build();
        var brainstorm = Card.builder()
                .name("Brainstorm")
                .typeLine("Instant")
                .oracleText("Draw three cards, then put two cards from your hand on top of your library in any order.")
                .build();
        var path = Card.builder()
                .name("Path to Exile")
                .typeLine("Instant")
                .oracleText("Exile target creature.")
                .build();
        var wr = Card.builder()
                .name("Wrath of God")
                .typeLine("Sorcery")
                .oracleText("Destroy all creatures. They can't be regenerated.")
                .build();

        var stats = DeckRoleStatsCalculator.fromCards(
                List.of(solRing, rampGrowth, brainstorm, path, wr));

        assertEquals(2, stats.ramp());
        assertEquals(1, stats.draw());
        assertEquals(1, stats.removal());
        assertEquals(1, stats.boardWipes());
    }
}
