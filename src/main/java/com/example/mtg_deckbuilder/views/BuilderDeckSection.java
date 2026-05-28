package com.example.mtg_deckbuilder.views;

import com.example.mtg_deckbuilder.dto.card.Card;

import java.util.List;

/**
 * Named bucket of deck cards produced from view options (“group by” / “sort by”).
 */
public record BuilderDeckSection(String title, List<Card> cards) {
}
