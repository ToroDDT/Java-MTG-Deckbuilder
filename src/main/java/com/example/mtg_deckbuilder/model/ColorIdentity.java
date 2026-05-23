package com.example.mtg_deckbuilder.model;

import com.example.mtg_deckbuilder.dto.card.Card;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum ColorIdentity {
    WHITE("W"),
    BLUE("U"),
    BLACK("B"),
    RED("R"),
    GREEN("G"),
    COLORLESS(""),
    Azorius("U,W"),
    RAKDOS("B,R"),
    GRUUL("G,R"),
    MARDU("B,R,W"),
    BOROS("R,W"),
    SIMIC("G,U"),
    GOLGARI("B,G"),
    Orzhov("B,W"),
    ABZAN("B,G,W"),
    BANT("G,U,W"),
    SELESNYA("G,W");


    private final String colorIdentity;

    ColorIdentity(String s) {
        this.colorIdentity = s;
    }

    public static ColorIdentity fromString(OwnedCard ownedCard) {
        List<String> colors = new ArrayList<>();
        String result = ownedCard.getCard().getColorIdentity()
                .stream()
                .map(s -> s.replaceAll("[{}]", ""))
                .collect(Collectors.joining(","));
        for (ColorIdentity c : values()) {
            if (result.contains(c.colorIdentity)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Unknown color identity: " + ownedCard.getCard().getColorIdentity());
    }

        public static List<String> getColors(Card card) {
        List<String> colors = new ArrayList<>();
        String result = card.getColorIdentity()
                .stream()
                .map(s -> s.replaceAll("[{}]", ""))
                .collect(Collectors.joining(","));
                if (result.contains("G")) {
                    colors.add("green");
                }
                if (result.contains("U")) {
                    colors.add("blue");
                }
                if (result.contains("R")) {
                    colors.add("red");
                }
                if (result.contains("B")) {
                    colors.add("black");
                }
                return colors;
    }
}