package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.model.*;
import com.example.mtg_deckbuilder.repository.MtgJsonRepository;
import com.example.mtg_deckbuilder.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CardService {

  private final MtgJsonRepository mtgJsonRepository;

  @Autowired
  public CardService(MtgJsonRepository mtgJsonRepository) {
    this.mtgJsonRepository = mtgJsonRepository;
  }

  public List<Card> findAllLegalCommanders() {
    return mtgJsonRepository.findLegalCommanderCards();
  }

  public Optional<Card> findColorIdentity(String name) {
    return mtgJsonRepository.findByColorIdentity(name);
  }


  public List<Card> executeComplexQuery(CardSearchParameters cardSearchParameters, Map<String, ?> params) {
    SqlBuilder sql = new SqlBuilder.Builder("SELECT * FROM card WHERE 1=1")
        .whereName(true, "farewell")
        .build();

    return mtgJsonRepository.executeComplexQuery(sql.getSql(), params);

  }
}
