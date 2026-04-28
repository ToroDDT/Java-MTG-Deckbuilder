package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.repository.api.BuilderRepository;
import com.example.mtg_deckbuilder.service.api.BuilderService;
import com.example.mtg_deckbuilder.views.BuilderViewModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BuilderServiceImpl implements BuilderService {

    private final BuilderRepository builderRepository;

    public BuilderServiceImpl(BuilderRepository builderRepository) {
        this.builderRepository = builderRepository;
    }

    @Override
    public BuilderViewModel getBuilderView(UUID userId) {
        List<OwnedCard> cards = builderRepository.getAllCardsForUser(userId);

        List<OwnedCard> creatures = cards.stream()
                .filter(card -> containsType(card, "Creature"))
                .toList();
        List<OwnedCard> spells = cards.stream()
                .filter(card -> containsType(card, "Instant"))
                .toList();
        List<OwnedCard> sorceries = cards.stream()
                .filter(card -> containsType(card, "Sorcery"))
                .toList();

        return new BuilderViewModel(creatures, spells, sorceries);
    }

    private boolean containsType(OwnedCard card, String type) {
        if (card.getCard() == null || card.getCard().getTypeLine() == null) {
            return false;
        }
        return card.getCard().getTypeLine().contains(type);
    }
}
