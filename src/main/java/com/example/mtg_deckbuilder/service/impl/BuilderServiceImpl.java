package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.repository.api.BuilderRepository;
import com.example.mtg_deckbuilder.service.api.BuilderService;
import com.example.mtg_deckbuilder.views.BuilderViewModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BuilderServiceImpl implements BuilderService {

    private final BuilderRepository builderRepository;

    public BuilderServiceImpl(BuilderRepository builderRepository) {
        this.builderRepository = builderRepository;
    }

    @Override
    public BuilderViewModel getBuilderView(String deckId ) {
        var cards = builderRepository.getAllCardsForUser(deckId);

         var creatures = cards.stream()
                .filter(card -> containsType(card, "Creature"))
                .toList();
         var instants = cards.stream()
                .filter(card -> containsType(card, "Instant"))
                .toList();
         var sorceries = cards.stream()
                .filter(card -> containsType(card, "Sorcery"))
                .toList();
         var enchantments = cards.stream()
                .filter(card -> containsType(card, "Enchantment"))
                .toList();
         var lands = cards.stream()
                .filter(card -> containsType(card, "Land"))
                .toList();
         var artifacts = cards.stream()
                .filter(card -> containsType(card, "Artifact"))
                .toList();


        return new BuilderViewModel(creatures, instants, enchantments, artifacts, lands, sorceries, deckId);
    }

    private boolean containsType(Map<String, String> card, String type) {
        if (card == null || card.get("type_line") == null) {
            return false;
        }
        return card.get("type_line").contains(type);
    }
}
