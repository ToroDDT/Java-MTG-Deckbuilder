package com.example.mtg_deckbuilder.model;

import java.util.Arrays;
import java.util.List;

public enum ColorIdentity {
   // Single Colors
    WHITE("{W}", "White", "W"),
    BLUE("{U}", "Blue", "U"),
    BLACK("{B}", "Black", "B"),
    RED("{R}", "Red", "R"),
    GREEN("{G}", "Green", "G"),
    COLORLESS("{}", "Colorless"),

    // Guilds (2-Color Pairs)
    AZORIUS("{W}{U}", "Azorius", "W", "U"),
    DIMIR("{U}{B}", "Dimir", "U", "B"),
    RAKDOS("{B}{R}", "Rakdos", "B", "R"),
    GRUUL("{R}{G}", "Gruul", "R", "G"),
    SELESNYA("{G}{W}", "Selesnya", "G", "W"),
    ORZHOV("{W}{B}", "Orzhov", "W", "B"),
    IZZET("{U}{R}", "Izzet", "U", "R"),
    GOLGARI("{B}{G}", "Golgari", "B", "G"),
    BOROS("{R}{W}", "Boros", "R", "W"),
    SIMIC("{G}{U}", "Simic", "G", "U");

    private final String nameBrackets;
    private final String colorIdentityName;
    private final List<String>colorIdentityAsList;

    ColorIdentity(String nameBrackets, String colorIdentityName,String... colorIdentityAsList) {
        this.nameBrackets = nameBrackets;
        this.colorIdentityName = colorIdentityName;
        this.colorIdentityAsList = Arrays.asList(colorIdentityAsList) ;
    };
}
