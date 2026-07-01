package com.example.mtg_deckbuilder.views.impl;

import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.views.api.DeckListItemView;

import java.util.List;

public record DeckListItemViewImpl(Deck deck, List<String> colors, double totalPrice) implements DeckListItemView {
}
