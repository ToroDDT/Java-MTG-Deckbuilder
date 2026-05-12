package com.example.mtg_deckbuilder.service.api;


import com.example.mtg_deckbuilder.model.cards.ScryfallCardObject;

import java.util.List;
import java.util.Optional;

public interface CardService {
    List<String> findLegalCommanders();
    Optional<ScryfallCardObject> findByName(String name);
    List<ScryfallCardObject> findByNameContaining(String name);
}
