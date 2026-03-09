package com.example.mtg_deckbuilder.utils;

import com.example.mtg_deckbuilder.model.Deck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class DeckUtils {
    public static List<Deck> filterDecks(List<Deck> decks, DeckSearchCriteria criteria) {
        String searchQuery = criteria.getSearchQuery();
        List<String> selectedColors = criteria.getSelectedColors();

        return decks.stream()
                .filter(deck -> matchesSearchQuery(deck, searchQuery))
                .filter(deck -> matchesSelectedColors(deck, selectedColors))
                .toList();
    }

    private static boolean matchesSearchQuery(Deck deck, String searchQuery) {
        if (searchQuery == null || searchQuery.isEmpty()) {
            return true;
        }
        return deck.name().toLowerCase().contains(searchQuery.toLowerCase());
    }

    private static boolean matchesSelectedColors(Deck deck, List<String> selectedColors) {
        if (selectedColors == null || selectedColors.isEmpty()) {
            return true;
        }
        return selectedColors.stream()
                .allMatch(color -> deck.colors_identity().contains(color));
    }

    static public List<Deck> sortDecks(List<Deck> decks, String sortBy, String sortOrder) {
        return switch (sortBy) {
            case "commander" -> decks.stream()
                    .sorted(Comparator.comparing(Deck::commander))
                    .toList();
            case "color identity" -> decks.stream()
                    .sorted(Comparator.comparing(Deck::colors_identity))
                    .toList();
            default -> decks;
        };
    }

    static public List<String> getColorIdentityOfDeck(Deck deck) {
        return Arrays.stream(deck.colors_identity().split(","))
                .map(String::trim)
                .toList();
    }
    static public List<List<String>> getColorIdentityOfDecks(List<Deck> decks) {
         List<List<String>> colorIdentityList = new ArrayList<>();
         for (Deck deck : decks){
             var colorIdentityAsArray = Arrays.stream(deck.colors_identity().split(","))
                     .map(String::trim)
                     .toList();
             colorIdentityList.add(colorIdentityAsArray);
         }
         return colorIdentityList;
    }
}
