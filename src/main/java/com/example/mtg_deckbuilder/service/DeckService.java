package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.exceptions.DeckDoesNotExistException;
import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.repository.DeckRepository;
import com.example.mtg_deckbuilder.utils.DeckSearchCriteria;
import com.example.mtg_deckbuilder.utils.DeckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

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

  public List<Deck> getAllDecksForUser (UUID userId) {
    return deckRepository.getAllDecksForUser(userId);
  }

  public void addCardToDeck(UUID deckId, UUID userId, UUID cardId, boolean isSideboard, UUID personalLibraryCardId) {
    var decks = this.getAllDecksForUser(userId);
    System.out.println("this is from the html   " + deckId);

    for (Deck deck : decks) {
      System.out.println(deck.id());
      if (deck.id().equals(deckId)) {
        deckRepository.addCardToDeck(deckId, cardId, isSideboard, personalLibraryCardId);
      }
    }
  }

  public Map<Deck, List<String>> getAllDecksForUser(UUID userId, DeckSearchCriteria deckSearchCriteria) {
    Map<Deck, List<String>> finalDecks = new LinkedHashMap<>();

    var decks = deckRepository.getAllDecksForUser(userId);
    var filteredDecks = DeckUtils.filterDecks(decks, deckSearchCriteria);
    var sortedDecks = DeckUtils.sortDecks(filteredDecks, deckSearchCriteria.getSortBy(), deckSearchCriteria.getSortOrder());

    var colorIdentityForEachDeck = DeckUtils.getColorIdentityOfDecks(sortedDecks);

    IntStream.range(0, sortedDecks.size())
            .forEach(i -> finalDecks.put(sortedDecks.get(i), colorIdentityForEachDeck.get(i)));
    return finalDecks;
  }
}