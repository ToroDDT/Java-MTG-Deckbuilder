package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.model.*;
import com.example.mtg_deckbuilder.repository.ScryfallRepository;
import com.example.mtg_deckbuilder.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ScryfallLibraryService {

  private final ScryfallRepository scryfallRepository;

  @Autowired
  public ScryfallLibraryService(ScryfallRepository scryfallRepository) {
    this.scryfallRepository = scryfallRepository;
  }

  @Cacheable("commanders")
  public List<String> findAllLegalCommanders() {
      return scryfallRepository.findLegalCommanderCards();
  }

  public Optional<Card> findByName(String name){
    return scryfallRepository.findByName(name);
  }

}
