package com.example.mtg_deckbuilder.service.api;

import com.example.mtg_deckbuilder.model.Card;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Optional;

public interface CardService {
    @Cacheable("commanders")
    List<String> findAllLegalCommanders();

    Optional<Card> findByName(String name);
}
