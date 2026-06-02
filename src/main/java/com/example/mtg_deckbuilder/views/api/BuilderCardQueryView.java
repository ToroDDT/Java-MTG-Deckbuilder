package com.example.mtg_deckbuilder.views.api;

import com.example.mtg_deckbuilder.dto.card.Card;

import java.util.List;

public interface BuilderCardQueryView {

    String query();

    List<Card> cards();
}
