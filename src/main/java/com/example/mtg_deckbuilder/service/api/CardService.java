package com.example.mtg_deckbuilder.service.api;

import com.example.mtg_deckbuilder.model.Card;

import java.util.List;
import java.util.Optional;

public interface CardService {
    List<String> findLegalCommanders();
    Optional<Card> findByName(String name);
}
