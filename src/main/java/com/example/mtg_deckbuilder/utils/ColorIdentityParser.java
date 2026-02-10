package com.example.mtg_deckbuilder.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorIdentityParser {
    public List<String> parseManaSymbols(String input) {
        if (input == null || input.isBlank()) return List.of();

        // Pattern to find things like {b}, {w}, {2/u}, etc.
        // Use [^{}]+ to allow for split mana like {b/g}
        Pattern pattern = Pattern.compile("\\{[^{}]+\\}");
        Matcher matcher = pattern.matcher(input.toLowerCase());

        List<String> manaSymbols= new ArrayList<>();
        while (matcher.find()) {
            manaSymbols.add(matcher.group());
        }
        return manaSymbols;
    }
}

