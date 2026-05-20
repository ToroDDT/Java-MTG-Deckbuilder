package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.repository.api.BuilderRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.BuilderService;
import com.example.mtg_deckbuilder.utils.DeckOptimizerV2;
import com.example.mtg_deckbuilder.views.BuilderCardHoverView;
import com.example.mtg_deckbuilder.views.BuilderDeckCardRecord;
import com.example.mtg_deckbuilder.views.BuilderViewModel;
import org.springframework.stereotype.Service;

import java.util.*;
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
    public Optional<BuilderCardHoverView> getDeckEntryHover(CustomUserDetails user, UUID deckId, UUID deckCardEntryId) {
        return builderRepository.findDeckEntryHover(user.getId(), deckId, deckCardEntryId);
    }


    @Override
    public List<OwnedCard>getCardsFromDeck(UUID deckId) {
        return builderRepository.getAllCardsFromDeck(deckId);
    }

    @Override
    public List<String> getRandomizedCards(UUID deckId) {
        var cards = builderRepository.getAllCardsFromDeck(deckId);
        Collections.shuffle(cards);
        // <img src> must be a plain https URL, not raw image_uris JSON (browser treats "{" as relative path).
        return cards.stream()
                .map(OwnedCard::getCard)
                .map(Card::artworkUrl)
                .filter(Objects::nonNull)
                .filter(img -> !img.isBlank())
                .limit(7)
                .toList();
    }

    @Override
    public BuilderViewModel getBuilderView(String deckId ) {
        var cards = builderRepository.getAllCardsForUser(deckId);
        if (cards.isEmpty()) {
            List<Long> emptyCurve = List.of(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
            return new BuilderViewModel(
                    null,
                    0.0,
                    "",
                    List.of(),
                    emptyCurve,
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    List.of(),
                    deckId);
        }

        var deckName = cards.getLast().deckName();
        var deckImage = cards.getLast().deckImage();

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
                .map(BuilderDeckCardRecord::priceUsd)
                .filter(Objects::nonNull)
                .mapToDouble(Double::parseDouble)
                .sum();
        Map<Integer, Long> manaCurve = cards.stream()
                .map(BuilderDeckCardRecord::cmc)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.groupingBy(
                        cmc -> (int) Double.parseDouble(cmc),
                        Collectors.counting()
                ));
        List<Long> manaCurveData = Stream.concat(
                IntStream.rangeClosed(0, manaCurve.size())
                        .mapToObj(i -> manaCurve.getOrDefault(i, 0L)),
                Stream.of(manaCurve.entrySet().stream()
                        .filter(e -> e.getKey() >= manaCurve.size())
                        .mapToLong(Map.Entry::getValue)
                        .sum())
        ).collect(Collectors.toList());

        return new BuilderViewModel(deckImage, total, deckName, creatures, manaCurveData, instants, enchantments, artifacts, lands, sorceries, deckId);
    }

    @Override
    public String optimizeDecksAgainstOpponent() {
        Map<String, Integer> oppDeck = new HashMap<>();

        oppDeck.put("1 CMC", 10);
        oppDeck.put("2 CMC", 15);
        oppDeck.put("3 CMC", 12);
        oppDeck.put("4 CMC", 8);
        oppDeck.put("5 CMC", 5);
        oppDeck.put("6 CMC", 2);
        oppDeck.put("Rock", 10);
        oppDeck.put("Draw", 0);
        oppDeck.put("Land", 36);
        oppDeck.put("Sol Ring", 1);
        DeckOptimizerV2 optimizer = new DeckOptimizerV2.Builder()
                .initial1Cmc(10)
                .initial2Cmc(15)
                .initial3Cmc(12)
                .initial4Cmc(8)
                .initial5Cmc(5)
                .initial6Cmc(2)
                .initialRock(10)
                .initialDraw(0)
                .initialLand(36)
                .commanderCost(4)
                .manaRockCost(2)
                .beatOtherMode(false)
                .oppCommander(5)
                .oppDecklist(oppDeck)
                .build();

        optimizer.run();
        return "";
    }

    @Override
    public String optimizeDeck() {
        return "";
    }

    private boolean containsType(BuilderDeckCardRecord card, String type) {
        if (card == null || card.typeLine() == null) {
            return false;
        }
        return card.typeLine().contains(type);
    }
}
