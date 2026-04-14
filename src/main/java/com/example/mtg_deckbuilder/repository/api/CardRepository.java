package com.example.mtg_deckbuilder.repository.api;

import com.example.mtg_deckbuilder.model.Card;

import java.util.Optional;
import java.util.UUID;

public interface CardRepository {
    Optional<Card> findByName(String name);
    Optional<Card> findById(UUID id);
}
