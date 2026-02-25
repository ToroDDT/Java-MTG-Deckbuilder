package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.repository.CardRepository;
import com.example.mtg_deckbuilder.repository.DeckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeckService {

  private final DeckRepository deckRepository;
  private final CardRepository cardRepository;
  private final CardService cardService;

  @Autowired
  public DeckService(DeckRepository deckRepository, CardRepository cardRepository, CardService cardService) {
    this.deckRepository = deckRepository;
    this.cardRepository = cardRepository;
    this.cardService = cardService;
  }

  public NewDeck addDeck(NewDeck newDeck) {
    return deckRepository.createNewDeckEntry(newDeck);
  }

}
