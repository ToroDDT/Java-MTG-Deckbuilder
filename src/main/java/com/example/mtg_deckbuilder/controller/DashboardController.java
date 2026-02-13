package com.example.mtg_deckbuilder.controller;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.repository.CardRepository;
import com.example.mtg_deckbuilder.utils.Autocomplete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
public class DashboardController {

    private final CardRepository cardRepository;
    private final Autocomplete autocomplete;

    @Autowired
    public DashboardController(CardRepository cardRepository, Autocomplete autocomplete) {
        this.cardRepository = cardRepository;
        this.autocomplete = autocomplete;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) throws IOException {
        model.addAttribute("cards", autocomplete.readFile());
        return "mtg-dashboard";
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        List<Card> results = cardRepository.findByCardsBySubstring(query);
        model.addAttribute("results", results);
        model.addAttribute("query", query);
        return "search-results";
    }


    @GetMapping("/mtg-dashboard")
    public String getPlayerProfile(Model model) throws IOException {

        model.addAttribute("cards", autocomplete.readFile());
        return "mtg-dashboard";
    }

}
