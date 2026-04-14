package com.example.mtg_deckbuilder.cache;

import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.repository.api.DeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class UserDecksCache {
    private final DeckRepository deckRepository;

    @Autowired
    public UserDecksCache(DeckRepository deckRepository){
        this.deckRepository = deckRepository;
    }
    @Cacheable("userId")
    public List<Deck> getAllDecksForUser (UUID userId) {
        return deckRepository.getAllDecksForUser(userId);
    }
}
