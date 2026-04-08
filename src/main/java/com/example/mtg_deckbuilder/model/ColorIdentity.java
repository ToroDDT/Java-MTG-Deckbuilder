package com.example.mtg_deckbuilder.model;

import lombok.Getter;

import java.util.stream.Collectors;

@Getter
public enum ColorIdentity {
    WHITE("W"),
    BLUE("U"),
    BLACK("B"),
    RED("R"),
    GREEN("G"),
    COLORLESS(""),
    RAKDOS("B,R"),
    SIMIC("G,U"),
    ABZAN("B,G,W");

    private final String colorIdentity;

    ColorIdentity(String s) {
        this.colorIdentity = s;
    }

    public static ColorIdentity fromString(OwnedCard ownedCard) {
        String result = ownedCard.getCard().getColorIdentity()
                .stream()
                .map(s -> s.replaceAll("[{}]", ""))
                .collect(Collectors.joining(","));
        for (ColorIdentity c : values()) {
            if (c.colorIdentity.equalsIgnoreCase(result)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown color identity: " + ownedCard.getCard().getColorIdentity());
    }
}