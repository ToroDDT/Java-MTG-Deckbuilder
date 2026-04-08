package com.example.mtg_deckbuilder.model;

import lombok.Getter;

@Getter
public enum SortOptions {

    PRICE_ASC("Price (Low-High) "),
    PRICE_DESC("Price (High-Low)"),
    CMC_ASC("CMC (Low-High)"),
    CMC_DESC("CMC (High-Low)"),
    NAME_ASC("Name (A-Z)"),
    NAME_DESC("Name (Z-A)"),;

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
        throw new IllegalArgumentException("Unknown sort type: " + text);
    }
}
