package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.utils.DeckUtils;
import com.example.mtg_deckbuilder.utils.DeckSearchCriteria;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class DeckController {

    @GetMapping("/decks")
    public String getDecks(@ModelAttribute("deckSearchCriteria") DeckSearchCriteria deckSearchCriteria, Model model) {
        List<Deck> decks = new ArrayList<>();

        Deck deck1 = new Deck(1L, "Galadriel Light of Valinor", Arrays.asList("W", "U", "G"), "Commander", 2, LocalDate.of(2024, 7, 15), "/decks/1");
        decks.add(deck1);

        List<Deck> filteredDecks = DeckUtils.filterDecks(decks, deckSearchCriteria);

        filteredDecks = DeckUtils.sortDecks(filteredDecks, deckSearchCriteria.getSortBy(), deckSearchCriteria.getSortOrder());

        model.addAttribute("decks", filteredDecks);
        model.addAttribute("deckSearchCriteria", deckSearchCriteria);

        return "decks";
    }
}
