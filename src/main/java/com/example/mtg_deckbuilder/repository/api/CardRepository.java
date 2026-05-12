package com.example.mtg_deckbuilder.repository.api;

import com.example.mtg_deckbuilder.model.cards.ScryfallCardObject;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardRepository {
    Optional<ScryfallCardObject> findByName(String name);
    Optional<ScryfallCardObject> findById(UUID id);
    List<ScryfallCardObject> findByCardsBySubstring(String name);
    List<String> findLegalCommanderCards();
}
