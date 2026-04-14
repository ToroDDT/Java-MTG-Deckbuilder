package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.model.*;
import com.example.mtg_deckbuilder.repository.impl.CardRepositoryImpl;
import com.example.mtg_deckbuilder.service.api.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CardServiceImpl implements CardService {

  private final CardRepositoryImpl scryfallRepository;

  @Autowired
  public CardServiceImpl(CardRepositoryImpl scryfallRepository) {
    this.scryfallRepository = scryfallRepository;
  }

  @Cacheable("commanders")
  @Override
  public List<String> findAllLegalCommanders() {
      return scryfallRepository.findLegalCommanderCards();
  }

  @Override
  public Optional<Card> findByName(String name){
    return scryfallRepository.findByName(name);
  }

}
