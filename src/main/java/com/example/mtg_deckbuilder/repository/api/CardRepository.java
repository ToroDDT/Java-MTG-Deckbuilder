package com.example.mtg_deckbuilder.repository.api;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.model.LibraryFilters;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository {
    Optional<Card> findByName(String name);
    Optional<Card> findById(UUID id);
    List<Card> findByCardsBySubstring(String name);
    List<String> findLegalCommanderCards();
    List<Card> findCardsPaginated();
    List<Card> findCards(LibraryFilters filters);
}
