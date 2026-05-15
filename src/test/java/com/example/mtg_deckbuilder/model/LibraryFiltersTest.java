package com.example.mtg_deckbuilder.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LibraryFiltersTest {

    @Test
    void hasSearchFilterReturnsTrueForPriceFilters() {
        LibraryFilters filters = new LibraryFilters();
        filters.setMinPrice(2.50);

        assertTrue(filters.hasSearchFilter());
    }

    @Test
    void hasSearchFilterReturnsTrueForLocationFilter() {
        LibraryFilters filters = new LibraryFilters();
        filters.setLocation("library");

        assertTrue(filters.hasSearchFilter());
    }
}
