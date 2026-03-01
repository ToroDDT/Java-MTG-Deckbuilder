package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.model.*;
import com.example.mtg_deckbuilder.repository.CardRepository;
import com.example.mtg_deckbuilder.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CardService {

  private final CardRepository cardRepository;

  @Autowired
  public CardService(CardRepository cardRepository) {
    this.cardRepository = cardRepository;
  }

  public List<Card> findAllLegalCommanders() {
    return cardRepository.findLegalCommanderCards();
  }

  public Optional<Card> findColorIdentity(String name) {
    return cardRepository.findByColorIdentity(name);
  }


  public List<Card> executeComplexQuery(CardSearchParameters cardSearchParameters, Map<String, ?> params) {
    SqlBuilder sql = new SqlBuilder.Builder("SELECT * FROM card WHERE 1=1")
        .whereName(true, "farewell")
        .build();

    return cardRepository.executeComplexQuery(sql.getSql(), params);

  }
}
