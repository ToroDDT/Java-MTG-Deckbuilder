package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.repository.CardRepository;
import com.example.mtg_deckbuilder.repository.DeckRepository;
import com.example.mtg_deckbuilder.utils.DeckSearchCriteria;
import com.example.mtg_deckbuilder.utils.DeckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DeckService {

  private final DeckRepository deckRepository;

  @Autowired
  public DeckService(DeckRepository deckRepository) {
    this.deckRepository = deckRepository;
  }

  public NewDeck addDeck(NewDeck newDeck) {
    return deckRepository.createNewDeckEntry(newDeck);
  }

  public Map<Deck,List<String>> getAllDecksForUser(UUID userId, DeckSearchCriteria deckSearchCriteria) {

    Map<Deck, List<String>> finalDecks = new HashMap<>();
    var decks = deckRepository.getAllDecksForUser(userId);
    var filteredDecks = DeckUtils.filterDecks(decks, deckSearchCriteria);
    var sortedDecks = DeckUtils.sortDecks(filteredDecks, deckSearchCriteria.getSortBy(), deckSearchCriteria.getSortOrder());
    var colorIdentityForEachDeck = DeckUtils.getColorIdentityOfDecks(sortedDecks);

    for (int i = 0; i < sortedDecks.toArray().length; i++) {
      finalDecks.put(sortedDecks.get(i),colorIdentityForEachDeck.get(i));
    }

    return finalDecks;
  }

}
