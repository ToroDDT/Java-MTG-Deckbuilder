package com.example.mtg_deckbuilder.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PersonalLibraryFilters {
    private String cardName;
    private List<String> selectedColors = new ArrayList<>();
    private String cardType;
    private Integer minCMC;
    private Integer maxCMC;
    private SortOptions sortBy;

    public PersonalLibraryFilters() {

    }
}
