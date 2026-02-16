package com.example.mtg_deckbuilder.controller;

import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.utils.FilterForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class DeckController {

    @GetMapping("/decks")
    public String getDecks(@ModelAttribute("filterForm") FilterForm filterForm, Model model) {
        // Get all decks
        List<Deck> allDecks = getDummyDecks();

        // Apply filters
        List<Deck> filteredDecks = filterDecks(allDecks, filterForm);

        // Apply sorting
        filteredDecks = sortDecks(filteredDecks, filterForm.getSortBy(), filterForm.getSortOrder());

        model.addAttribute("decks", filteredDecks);
        model.addAttribute("filterForm", filterForm);

        return "decks";
    }

    private List<Deck> getDummyDecks() {
        List<Deck> decks = new ArrayList<>();

        Deck deck1 = new Deck(1L, "Galadriel Light of Valinor", Arrays.asList("W", "U", "G"), "Commander", 2, LocalDate.of(2024, 7, 15), "/decks/1");
        decks.add(deck1);

        return decks;
    }

    private List<Deck> filterDecks(List<Deck> decks, FilterForm filterForm) {
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
                            if (!deck.colors().contains(color)) {
                                return false;
                            }
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    private List<Deck> sortDecks(List<Deck> decks, String sortBy, String sortOrder) {
        List<Deck> sorted = new ArrayList<>(decks);

        if ("name".equals(sortBy)) {
            sorted.sort((a, b) -> a.name().compareToIgnoreCase(b.name()));
        } else if ("lastUpdated".equals(sortBy)) {
            sorted.sort((a, b) -> a.lastUpdated().compareTo(b.lastUpdated()));
        }

        if ("desc".equals(sortOrder)) {
            java.util.Collections.reverse(sorted);
        }

        return sorted;
    }
}
