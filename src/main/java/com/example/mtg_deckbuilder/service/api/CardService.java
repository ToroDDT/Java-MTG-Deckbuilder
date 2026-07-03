package com.example.mtg_deckbuilder.service.api;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.model.LibraryFilters;

import java.util.List;
import java.util.Optional;

public interface CardService {
    List<String> findLegalCommanders();
    Optional<Card> findByName(String name);
    List<Card> findByNameContaining(String name);
    List<Card> findCardsPaginated();
    List<Card> findCards(LibraryFilters filters);
}
