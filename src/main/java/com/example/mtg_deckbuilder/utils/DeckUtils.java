package com.example.mtg_deckbuilder.utils;

import com.example.mtg_deckbuilder.model.Deck;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeckUtils {
    public static boolean matchesSearchQuery(Deck deck, String searchQuery) {
        if (searchQuery == null || searchQuery.isEmpty()) {
            return true;
        }
        return deck.name().toLowerCase().contains(searchQuery.toLowerCase());
    }

    public static boolean matchesSelectedColors(Deck deck, List<String> selectedColors) {
        if (selectedColors == null || selectedColors.isEmpty()) {
            return true;
        }
        return selectedColors.stream() .allMatch(color -> deck.colors_identity().contains(color));
    }

    public static boolean matchesFolder(Deck deck, String folder) {
        if (folder == null || folder.isEmpty() || folder.equals("home")) {
            return true;
        }
        return deck.folder().contains(folder);
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
