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

        Deck deck1 = new Deck();
        deck1.setId(1L);
        deck1.setName("Galadriel Light of Valinor");
        deck1.setColors(Arrays.asList("W", "U", "G"));
        deck1.setFormat("Commander");
        deck1.setBracket(2);
        deck1.setLastUpdated(LocalDate.of(2024, 7, 15));
        deck1.setUrl("/decks/1");
        decks.add(deck1);

        Deck deck2 = new Deck();
        deck2.setId(2L);
        deck2.setName("Kroxa and Kunoros");
        deck2.setColors(Arrays.asList("B", "R", "W"));
        deck2.setFormat("Commander");
        deck2.setBracket(null);
        deck2.setLastUpdated(LocalDate.of(2024, 9, 15));
        deck2.setUrl("/decks/2");
        decks.add(deck2);

        Deck deck3 = new Deck();
        deck3.setId(3L);
        deck3.setName("kroxa");
        deck3.setColors(Arrays.asList("B", "R"));
        deck3.setFormat("Commander");
        deck3.setBracket(null);
        deck3.setLastUpdated(LocalDate.of(2024, 10, 15));
        deck3.setUrl("/decks/3");
        decks.add(deck3);

        Deck deck4 = new Deck();
        deck4.setId(4L);
        deck4.setName("Necrobloom");
        deck4.setColors(Arrays.asList("B", "G"));
        deck4.setFormat("Commander");
        deck4.setBracket(null);
        deck4.setLastUpdated(LocalDate.of(2024, 10, 15));
        deck4.setUrl("/decks/4");
        decks.add(deck4);

        Deck deck5 = new Deck();
        deck5.setId(5L);
        deck5.setName("Galadriel, Light of Valinor");
        deck5.setColors(Arrays.asList("W", "U", "G"));
        deck5.setFormat("Commander");
        deck5.setBracket(null);
        deck5.setLastUpdated(LocalDate.of(2024, 10, 15));
        deck5.setUrl("/decks/5");
        decks.add(deck5);

        return decks;
    }

    private List<Deck> filterDecks(List<Deck> decks, FilterForm filterForm) {
        return decks.stream()
                .filter(deck -> {
                    // Filter by search query
                    if (filterForm.getSearchQuery() != null && !filterForm.getSearchQuery().isEmpty()) {
                        if (!deck.getName().toLowerCase().contains(filterForm.getSearchQuery().toLowerCase())) {
                            return false;
                        }
                    }

                    // Filter by colors
                    if (filterForm.getSelectedColors() != null && !filterForm.getSelectedColors().isEmpty()) {
                        for (String color : filterForm.getSelectedColors()) {
                            if (!deck.getColors().contains(color)) {
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
            sorted.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        } else if ("lastUpdated".equals(sortBy)) {
            sorted.sort((a, b) -> a.getLastUpdated().compareTo(b.getLastUpdated()));
        }

        if ("desc".equals(sortOrder)) {
            java.util.Collections.reverse(sorted);
        }

        return sorted;
    }
}
