package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.model.*;
import com.example.mtg_deckbuilder.repository.ScryfallRepository;
import com.example.mtg_deckbuilder.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CardService {

  private final ScryfallRepository scryfallRepository;

  @Autowired
  public CardService(ScryfallRepository scryfallRepository) {
    this.scryfallRepository = scryfallRepository;
  }

  public List<Card> findAllLegalCommanders() {

    var cards = scryfallRepository.findLegalCommanderCards();
    for ( Card card : cards){
      System.out.println(card.getName());
    }
    return cards;
  }

  public Optional<Card> findColorIdentity(String name) {
    return scryfallRepository.findByColorIdentity(name);
  }


  public List<Card> executeComplexQuery(CardSearchParameters cardSearchParameters, Map<String, ?> params) {
    SqlBuilder sql = new SqlBuilder.Builder("SELECT * FROM card WHERE 1=1")
        .whereName(true, "farewell")
        .build();

    return scryfallRepository.executeComplexQuery(sql.getSql(), params);

  }
}
