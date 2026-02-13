package com.example.mtg_deckbuilder.model;

import lombok.Getter;

@Getter
public enum CardType {
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

    // Constructor - Note: Enum constructors are always private
    CardType(String type) {
        this.type = type;
    }

    public static CardType fromString(String text) {
        for (CardType cardType: CardType.values()) {
            if (cardType.getType().equalsIgnoreCase(text)) {
                return cardType;
            }
        }
        return null;
    }
}
