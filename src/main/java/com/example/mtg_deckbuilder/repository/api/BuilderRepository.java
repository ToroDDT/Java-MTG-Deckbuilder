package com.example.mtg_deckbuilder.repository.api;

import com.example.mtg_deckbuilder.model.OwnedCard;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface BuilderRepository {
    List<Map<String, String>> getAllCardsForUser(String deckId);
}
