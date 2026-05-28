package com.example.mtg_deckbuilder.views;

import com.example.mtg_deckbuilder.dto.card.Card;

import java.util.List;

public record BuilderCardQueryView(
        String query,
        List<Card> cards
) {
    public static BuilderCardQueryView of(String query, List<Card> cards) {
        return new BuilderCardQueryView(getString(query), cards);
    }

    public static String getString(String query) {
        return query == null ? "" : query.trim();
    }
}
