package com.example.mtg_deckbuilder.views;

import java.util.List;

/**
 * Named bucket of deck cards produced from view options (“group by” / “sort by”).
 */
public record BuilderDeckSection(String title, List<BuilderDeckCardRecord> cards) {
}
