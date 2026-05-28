package com.example.mtg_deckbuilder.views;


import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.model.ColorIdentity;
import lombok.Builder;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Builder
public record BuilderViewModel(
        String image,
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
        List<String> colors
) {
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

    public static BuilderViewModel empty(String deckId) {
        return BuilderViewModel.builder()
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
        return BuilderViewModel.builder()
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
                .build();
    }

    public static BuilderViewModel fromCards(String deckId,
                                             List<Card> cards,
                                             Function<String, Optional<Card>> findCardByName) {

        if (cards.isEmpty()) {
            return BuilderViewModel.empty(deckId);
        }
        var deckImage = cards.getLast().getDeckImage();
        var deckName = cards.getLast().getDeckName();
        var cardTypes = filterTypes(cards);
        var colorProduction = calculateColorProduction(cards);
        return BuilderViewModel.builder()
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
