package com.example.mtg_deckbuilder.views.impl;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.views.api.BuilderDeckSection;

import java.util.List;

public record BuilderDeckSectionImpl(String title, List<Card> cards) implements BuilderDeckSection {
}
