package com.example.mtg_deckbuilder.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum CardType {
    ALL("All"),
    SORCERY("Sorcery"),
    INSTANT("Instant"),
    CREATURE("Creature"),
    ENCHANTMENT("Enchantment"),
    KINDRED("Kindred"),
    LAND("Land"),
    PLANESWALKER("Planeswalker"),
    PLANE("Plane"),
    BATTLE("Battle");

    private final String type;

    CardType(String type) {
        this.type = type;
    }

    public static CardType fromString(String text) {
        for (CardType cardType: CardType.values()) {
            if (cardType.getType().equalsIgnoreCase(text)) {
                return cardType;
            }
        }
        throw new IllegalArgumentException("Unknown card type: " + text);
    }
}
