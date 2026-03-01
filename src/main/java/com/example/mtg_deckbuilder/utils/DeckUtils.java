package com.example.mtg_deckbuilder.utils;

import com.example.mtg_deckbuilder.model.Deck;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DeckUtils {
     static public List<Deck> filterDecks(List<Deck> decks, DeckSearchCriteria filterForm) {
        return decks.stream()
                .filter(deck -> {
                    // Filter by search query
                    if (filterForm.getSearchQuery() != null && !filterForm.getSearchQuery().isEmpty()) {
                        if (!deck.name().toLowerCase().contains(filterForm.getSearchQuery().toLowerCase())) {
                            return false;
                        }
                    }

                    // Filter by colors
                    if (filterForm.getSelectedColors() != null && !filterForm.getSelectedColors().isEmpty()) {
                        for (String color : filterForm.getSelectedColors()) {
                            if (!deck.colors_identity().contains(color)) {
                                return false;
                            }
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    static public List<Deck> sortDecks(List<Deck> decks, String sortBy, String sortOrder) {
        List<Deck> sorted = new ArrayList<>(decks);

        if ("name".equals(sortBy)) {
            sorted.sort((a, b) -> a.name().compareToIgnoreCase(b.name()));
        } else if ("lastUpdate".equals(sortBy)) {
            sorted.sort((a, b) -> a.last_updated().compareTo(b.last_updated()));
        }

        if ("desc".equals(sortOrder)) {
            java.util.Collections.reverse(sorted);
        }
        return sorted;
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
