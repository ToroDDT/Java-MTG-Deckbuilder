package com.example.mtg_deckbuilder.utils;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

import java.util.List;

@Getter
@Setter
public class DeckSearchCriteria {
    private String searchQuery;
    private List<String> selectedColors = new ArrayList<>();
    private String sortBy = "lastUpdate";
    private String sortOrder = "desc";

    // Constructors
    public DeckSearchCriteria() {
    }

    public DeckSearchCriteria(String searchQuery, List<String> selectedColors, String sortBy, String sortOrder) {
        this.searchQuery = searchQuery;
        this.selectedColors = selectedColors != null ? selectedColors : new ArrayList<>();
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

}