package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.cache.UserDecksCache;
import com.example.mtg_deckbuilder.exceptions.DeckDoesNotExistException;
import com.example.mtg_deckbuilder.model.AddCardToDeckRequest;
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
public class DefaultDeckService implements DeckService {

  private final DeckRepository deckRepository;
  private final UserDecksCache userDecksCache;


   @Autowired
  public DefaultDeckService(DeckRepository deckRepository, UserDecksCache userDecksCache) {
    this.deckRepository = deckRepository;
    this.userDecksCache = userDecksCache;
  }

  @Override
  public void addDeck(NewDeck newDeck) {
    deckRepository.createNewDeckEntry(newDeck);
  }

  @Override
  public void addCardToDeck(AddCardToDeckRequest cardRequest) {

      userDecksCache.getAllDecksForUser(cardRequest.userId()).stream()
            .filter(deck -> deck.id().equals(cardRequest.deckId()))
            .findFirst()
            .orElseThrow(() -> new DeckDoesNotExistException(cardRequest.deckId()));

    deckRepository.addCardToDeck(cardRequest);
 }

 @Override
  public Map<Deck, List<String>> getAllDecksForUser(UUID user, DeckSearchCriteria deckSearchCriteria) {
    Map<Deck, List<String>> finalDecks = new LinkedHashMap<>();

    var decks = deckRepository.getAllDecksForUser(user);
    var filteredDecks = DeckUtils.filterDecks(decks, deckSearchCriteria);
    var sortedDecks = DeckUtils.sortDecks(filteredDecks, deckSearchCriteria.getSortBy());

    var colorIdentityForEachDeck = DeckUtils.getColorIdentityOfDecks(sortedDecks);

    IntStream.range(0, sortedDecks.size())
            .forEach(i -> finalDecks.put(sortedDecks.get(i), colorIdentityForEachDeck.get(i)));
    return finalDecks;
  }
}