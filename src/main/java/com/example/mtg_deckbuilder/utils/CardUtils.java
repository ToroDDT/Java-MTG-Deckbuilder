package com.example.mtg_deckbuilder.utils;

import com.example.mtg_deckbuilder.model.CardType;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.model.LibraryFilters;

import java.util.HashSet;
import java.util.List;

public class CardUtils {

    public static boolean matchesSearchQuery(OwnedCard ownedCard, String searchQuery) {
        if (searchQuery == null || searchQuery.isEmpty()) {
            return true;
        }
        return ownedCard.getCard()
                .getName()
                .toLowerCase()
                .startsWith(searchQuery.toLowerCase());
    }
    public static boolean matchesSelectedColors(OwnedCard ownedCard, List<String> selectedColors) {
        if (selectedColors == null || selectedColors.isEmpty()) {
            return true;
        }
        List<String> colorIdentity = ownedCard.getCard().getColorIdentity();
        return new HashSet<>(colorIdentity).containsAll(selectedColors) && new HashSet<>(selectedColors).containsAll(colorIdentity);
    }

    public static boolean matchesSelectedType(OwnedCard ownedCard, CardType cardType) {
        if (cardType == null || CardType.ALL.equals(cardType)) {
            return  true;
        }
        return ownedCard.getCard()
                .getTypeLine()
                .contains(cardType.getType());
    }
    public static boolean matchesCmcRange(OwnedCard ownedCard, LibraryFilters personalLibraryFilters) {
        return ownedCard.getCard().getCmc() >= personalLibraryFilters.getMinCMC() && ownedCard.getCard().getCmc() <= personalLibraryFilters.getMaxCMC();
    }

}
