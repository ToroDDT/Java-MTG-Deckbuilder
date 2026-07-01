package com.example.mtg_deckbuilder.views.api;

import com.example.mtg_deckbuilder.model.Deck;

import java.util.List;

public interface DeckListItemView {

    Deck deck();

    List<String> colors();

    double totalPrice();
}
