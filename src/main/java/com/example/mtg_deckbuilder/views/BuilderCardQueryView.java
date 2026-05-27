package com.example.mtg_deckbuilder.views;

import com.example.mtg_deckbuilder.dto.card.Card;

import java.util.List;

public record BuilderCardQueryView(
        String query,
        List<Card> cards
) {
}
