package com.example.mtg_deckbuilder.views.impl;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.dto.combo.CardCombos;
import com.example.mtg_deckbuilder.model.ColorIdentity;
import com.example.mtg_deckbuilder.views.api.BuilderViewModel;
import com.example.mtg_deckbuilder.views.api.DeckRoleStats;
import lombok.Builder;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Builder
public record BuilderViewModelImpl(
        String image,
        int bracketInfo,
        Double totalValue,
        String deckName,
        List<Card> creatures,
        List<Long> manaCurveData,
        List<Card> instants,
        List<Card> enchantments,
        List<Card> artifacts,
        List<Card> lands,
        List<Card> sorceries,
        List<Long> colorProduction,
        String deckId,
        List<String> colors,
        DeckRoleStats roleStats,
        int deckSize
) implements BuilderViewModel {

    @Builder
    public record CardTypes(List<Card> creatures,
                            List<Card> artifacts,
                            List<Card> lands,
                            List<Card> enchantments,
                            List<Card> sorceries,
                            List<Card> instants) {
    }

    private static final List<Long> EMPTY_MANA_CURVE =
            List.of(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
    private static final List<Long> EMPTY_COLOR_PRODUCTION =
            List.of(0L, 0L, 0L, 0L, 0L, 0L);

    private record ColorProduction(long red, long white, long green, long black, long blue, long colorless) {

        ColorProduction combine(ColorProduction other) {
            return new ColorProduction(
                    red + other.red(),
                    white + other.white(),
                    green + other.green(),
                    black + other.black(),
                    blue + other.blue(),
                    colorless + other.colorless());
        }

        static ColorProduction empty() {
            return new ColorProduction(0, 0, 0, 0, 0, 0);
        }

        static ColorProduction fromIdentity(String identity) {
            if (identity == null || identity.isBlank()) {
                return new ColorProduction(0, 0, 0, 0, 0, 1);
            }
            return new ColorProduction(
                    identity.contains("R") ? 1 : 0,
                    identity.contains("W") ? 1 : 0,
                    identity.contains("G") ? 1 : 0,
                    identity.contains("B") ? 1 : 0,
                    identity.contains("U") ? 1 : 0,
                    0);
        }
    }

    private static final Pattern KEYWORD_EXTRA_TURN = Pattern.compile("(?=.*Extra)(?=.*turn)", Pattern.CASE_INSENSITIVE);
    private static final Pattern KEYWORD_DESTROY_LANDS = Pattern.compile(
            "(?=.*(?:destroy|exile))(?=.*all)(?=.*lands?\\b)",
            Pattern.CASE_INSENSITIVE
    );

    public static int calculateBracketInfo(List<Card> deckCards, CardCombos userCombos, String deckName) {
        AtomicInteger amountOfGameChanger = new AtomicInteger();
        AtomicBoolean containsLandDenial = new AtomicBoolean(false);
        AtomicBoolean containsTwoCardCombo = new AtomicBoolean(false);
        AtomicBoolean containsExtraTurns = new AtomicBoolean(false);

        deckCombos(userCombos, deckName).forEach(combo -> {
            if (combo.size() == 2) {
                containsTwoCardCombo.set(true);
            }
        });

        deckCards.forEach(card -> {
            if (card.isGameChanger()) {
                amountOfGameChanger.incrementAndGet();
            }
            String text = card.getOracleText();
            if (text != null && KEYWORD_DESTROY_LANDS.matcher(text).find()) {
                containsLandDenial.set(true);
            }
            if (text != null && KEYWORD_EXTRA_TURN.matcher(text).find()) {
                containsExtraTurns.set(true);
            }
        });

        if (amountOfGameChanger.get() == 0 && !containsLandDenial.get() && !containsTwoCardCombo.get() && !containsExtraTurns.get()) {
            return 1;
        } else if (amountOfGameChanger.get() == 0 && !containsLandDenial.get() && !containsTwoCardCombo.get()) {
            return 2;
        } else if (amountOfGameChanger.get() <= 3 && !containsLandDenial.get() && !containsTwoCardCombo.get()) {
            return 3;
        } else if (amountOfGameChanger.get() >= 4 || !containsLandDenial.get() && !containsTwoCardCombo.get()) {
            return 4;
        }
        return 5;
    }

    private static List<List<String>> deckCombos(CardCombos userCombos, String deckName) {
        if (userCombos == null || userCombos.getCardCombinations() == null || deckName == null || deckName.isBlank()) {
            return List.of();
        }
        List<String> locations = userCombos.getLocations() == null ? List.of() : userCombos.getLocations();
        List<List<String>> deckOnly = new ArrayList<>();
        var combinations = userCombos.getCardCombinations();
        for (int i = 0; i < combinations.size(); i++) {
            String location = i < locations.size() ? locations.get(i) : userCombos.getLocation();
            if (deckName.equals(location)) {
                deckOnly.add(combinations.get(i));
            }
        }
        return deckOnly;
    }

    public static BuilderViewModel empty(String deckId) {
        return BuilderViewModelImpl.builder()
                .image("")
                .deckId(deckId)
                .manaCurveData(EMPTY_MANA_CURVE)
                .lands(List.of())
                .artifacts(List.of())
                .creatures(List.of())
                .instants(List.of())
                .colorProduction(EMPTY_COLOR_PRODUCTION)
                .enchantments(List.of())
                .colors(List.of())
                .sorceries(List.of())
                .totalValue(0.0)
                .deckName("")
                .bracketInfo(1)
                .roleStats(DeckRoleStats.EMPTY)
                .deckSize(0)
                .build();
    }

    public static BuilderViewModel of(String deckId,
                                      String deckName,
                                      String image,
                                      Double totalValue,
                                      List<Card> creatures,
                                      List<Long> manaCurveData,
                                      List<Card> instants,
                                      List<Card> enchantments,
                                      List<Card> artifacts,
                                      List<Card> lands,
                                      List<Card> sorceries,
                                      List<Long> colorProduction,
                                      List<String> colors) {
        return BuilderViewModelImpl.builder()
                .image(image)
                .totalValue(totalValue)
                .deckName(deckName)
                .creatures(creatures)
                .manaCurveData(manaCurveData)
                .instants(instants)
                .colors(colors)
                .enchantments(enchantments)
                .artifacts(artifacts)
                .lands(lands)
                .colorProduction(colorProduction)
                .sorceries(sorceries)
                .deckId(deckId)
                .bracketInfo(1)
                .roleStats(DeckRoleStats.EMPTY)
                .deckSize(0)
                .build();
    }

    public static BuilderViewModel fromCards(String deckId,
                                             List<Card> cards,
                                             Function<String, Optional<Card>> findCardByName) {
        return fromCards(deckId, cards, findCardByName, null);
    }

    public static BuilderViewModel fromCards(String deckId,
                                             List<Card> cards,
                                             Function<String, Optional<Card>> findCardByName,
                                             CardCombos userCombos) {

        if (cards.isEmpty()) {
            return BuilderViewModelImpl.empty(deckId);
        }
        var deckImage = cards.getLast().getDeckImage();
        var deckName = cards.getLast().getDeckName();
        var cardTypes = filterTypes(cards);
        var colorProduction = calculateColorProduction(cards);
        int bracketInfo = calculateBracketInfo(cards, userCombos, deckName);
        return BuilderViewModelImpl.builder()
                .deckId(deckId)
                .deckName(deckName)
                .image(deckImage)
                .creatures(cardTypes.creatures())
                .enchantments(cardTypes.enchantments())
                .artifacts(cardTypes.artifacts())
                .lands(cardTypes.lands())
                .instants(cardTypes.instants())
                .sorceries(cardTypes.sorceries())
                .manaCurveData(calculateManaCurve(cards))
                .totalValue(calculateTotal(cards))
                .colorProduction(colorProduction)
                .colors(findColors(cards, findCardByName))
                .bracketInfo(bracketInfo)
                .roleStats(DeckRoleStatsCalculator.fromCards(cards))
                .deckSize(cards.size())
                .build();
    }

    public static CardTypes filterTypes(List<Card> cards) {
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
        return CardTypes.builder()
                .creatures(creatures)
                .instants(instants)
                .sorceries(sorceries)
                .enchantments(enchantments)
                .lands(lands)
                .artifacts(artifacts)
                .build();
    }

    public static Double calculateTotal(List<Card> cards) {
        return cards.stream()
                .map(Card::getPriceUsd)
                .filter(Objects::nonNull)
                .mapToDouble(Double::parseDouble)
                .sum();
    }

    private static ColorProduction getColorProduction(List<Card> cards) {
        return cards.stream()
                .map(card -> ColorProduction.fromIdentity(card.getProducedMana()))
                .reduce(ColorProduction.empty(), ColorProduction::combine);
    }

    public static List<Long> calculateColorProduction(List<Card> cards) {
        var colorProduction = getColorProduction(cards.stream()
                .filter(card -> containsType(card, "Land"))
                .toList());
        return List.of(
                colorProduction.red(),
                colorProduction.white(),
                colorProduction.green(),
                colorProduction.black(),
                colorProduction.blue(),
                colorProduction.colorless()
        );
    }

    public static List<Long> calculateManaCurve(List<Card> cards) {
        var manaCurve = cards.stream()
                .filter(card -> card.getTypeLine() == null || !card.getTypeLine().contains("Land"))
                .map(Card::getCmc)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        cmc -> cmc,
                        Collectors.counting()
                ));
        return Stream.concat(
                IntStream.rangeClosed(0, manaCurve.size())
                        .mapToObj(i -> manaCurve.getOrDefault(i, 0L)),
                Stream.of(manaCurve.entrySet().stream()
                        .filter(e -> e.getKey() >= manaCurve.size())
                        .mapToLong(Map.Entry::getValue)
                        .sum())
        ).collect(Collectors.toList());
    }

    private static List<String> findColors(List<Card> cards,
                                           Function<String, Optional<Card>> findCardByName) {
        if (cards.isEmpty() || cards.getLast().getCommander() == null) {
            return List.of();
        }
        return findCardByName.apply(cards.getLast().getCommander())
                .map(ColorIdentity::getColors)
                .orElse(List.of());
    }

    private static boolean containsType(Card card, String type) {
        if (card == null || card.getTypeLine() == null) {
            return false;
        }
        return card.getTypeLine().contains(type);
    }
}
