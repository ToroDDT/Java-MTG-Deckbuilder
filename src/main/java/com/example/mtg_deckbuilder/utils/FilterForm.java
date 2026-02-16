package com.example.mtg_deckbuilder.utils;
import java.util.ArrayList;

import java.util.ArrayList;
import java.util.List;

public class FilterForm {
    private String searchQuery;
    private List<String> selectedColors = new ArrayList<>();
    private String sortBy = "lastUpdated";
    private String sortOrder = "desc";

    // Constructors
    public FilterForm() {
    }

    public FilterForm(String searchQuery, List<String> selectedColors, String sortBy, String sortOrder) {
        this.searchQuery = searchQuery;
        this.selectedColors = selectedColors != null ? selectedColors : new ArrayList<>();
        this.sortBy = sortBy;
        this.sortOrder = sortOrder;
    }

    // Getters and Setters
    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public List<String> getSelectedColors() {
        return selectedColors;
    }

    public void setSelectedColors(List<String> selectedColors) {
        this.selectedColors = selectedColors;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    @Override
    public String toString() {
        return "FilterForm{" +
                "searchQuery='" + searchQuery + '\'' +
                ", selectedColors=" + selectedColors +
                ", sortBy='" + sortBy + '\'' +
                ", sortOrder='" + sortOrder + '\'' +
                '}';
    }
}