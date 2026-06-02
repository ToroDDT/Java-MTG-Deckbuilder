package com.example.mtg_deckbuilder.views.impl;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.views.api.BuilderCardQueryView;

import java.util.List;

public record BuilderCardQueryViewImpl(String query, List<Card> cards) implements BuilderCardQueryView {

    public static BuilderCardQueryView of(String query, List<Card> cards) {
        return new BuilderCardQueryViewImpl(getString(query), cards);
    }

    public static String getString(String query) {
        return query == null ? "" : query.trim();
    }
}
