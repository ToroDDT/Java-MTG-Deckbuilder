package com.example.mtg_deckbuilder.views.api;

import com.example.mtg_deckbuilder.dto.card.Card;

import java.util.List;

public interface BuilderViewModel {

    String image();

    Double totalValue();

    String deckName();

    List<Card> creatures();

    List<Long> manaCurveData();

    List<Card> instants();

    List<Card> enchantments();

    List<Card> artifacts();

    List<Card> lands();

    List<Card> sorceries();

    List<Long> colorProduction();

    String deckId();

    List<String> colors();
}
