package com.example.mtg_deckbuilder.model;

import lombok.Getter;

@Getter
public enum SortOptions {

    PRICELOW("Price (Low-High) "),
    PRICEHIGH("Price (High-Low)"),
    CMCLOW("CMC (Low-High)"),
    CMCHIGH("CMC (High-Low)");

    private final String sortType;

    SortOptions(String sortType) {
        this.sortType= sortType;
    }

    public static SortOptions getSortType(String text) {
        for (SortOptions sortType: SortOptions.values()) {
            if (sortType.getSortType().equalsIgnoreCase(text)) {
                return sortType;
            }
        }
        return null;
    }
}
