package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.repository.api.BuilderRepository;
import com.example.mtg_deckbuilder.service.api.BuilderService;
import com.example.mtg_deckbuilder.views.BuilderViewModel;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class BuilderServiceImpl implements BuilderService {

    private final BuilderRepository builderRepository;

    public BuilderServiceImpl(BuilderRepository builderRepository) {
        this.builderRepository = builderRepository;
    }

    @Override
    public BuilderViewModel getBuilderView(String deckId ) {
        var cards = builderRepository.getAllCardsForUser(deckId);
        var deckName = cards.getLast().get("deck_name");

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
        var total = cards.stream()
                .filter(card -> card.get("prices") != null)
                .mapToDouble(card -> (Double.parseDouble(card.get("prices"))))
                .sum();
        Map<Integer, Long> manaCurve = cards.stream()
                .filter(card -> card.get("cmc") != null)
                .collect(Collectors.groupingBy(
                        card -> (int) Double.parseDouble(card.get("cmc")),
                        Collectors.counting()
                ));       List<Long> manaCurveData = Stream.concat(
                IntStream.rangeClosed(0, manaCurve.size())
                        .mapToObj(i -> manaCurve.getOrDefault(i, 0L)),
                Stream.of(manaCurve.entrySet().stream()
                        .filter(e -> e.getKey() >= manaCurve.size())
                        .mapToLong(Map.Entry::getValue)
                        .sum())
        ).collect(Collectors.toList());

        return new BuilderViewModel(total, deckName, creatures, manaCurveData, instants, enchantments, artifacts, lands, sorceries, deckId);
    }

    private boolean containsType(Map<String, String> card, String type) {
        if (card == null || card.get("type_line") == null) {
            return false;
        }
        return card.get("type_line").contains(type);
    }
}
