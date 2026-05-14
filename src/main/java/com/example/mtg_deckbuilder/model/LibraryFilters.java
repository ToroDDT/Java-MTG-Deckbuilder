package com.example.mtg_deckbuilder.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class LibraryFilters {
    private String cardName;
    private List<String> selectedColors = new ArrayList<>();
    private String cardType;
    private Integer minCMC;
    private Integer maxCMC;
    private Double minPrice;
    private Double maxPrice;
    private String location;
    private SortOptions sortBy;
    private String operator;
    private String dateAdded;
    private Integer page = 0;
    private String tagSearch;
    private String oracleTextSearch;

    public LibraryFilters() {

    }

    public List<String> tagSearchTokens() {
        if (tagSearch == null || tagSearch.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tagSearch.split("[,;]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

   public boolean hasSearchFilter() {
    boolean hasTagSearch = !tagSearchTokens().isEmpty();
    return (oracleTextSearch != null && !oracleTextSearch.isEmpty() || cardName != null && !cardName.isEmpty()) ||
            (cardType != null && !cardType.isEmpty() && !"All".equalsIgnoreCase(cardType)) || // "All" not "All Types"
            (selectedColors != null && !selectedColors.isEmpty()) ||
            (minCMC != null && minCMC > 0) ||
            (maxCMC != null && maxCMC < 16)
            || (minPrice != null && minPrice > 0)
            || (maxPrice != null && maxPrice > 0)
            || (location != null && !location.isBlank() && !"All".equalsIgnoreCase(location))
            || hasTagSearch;
 }
}
