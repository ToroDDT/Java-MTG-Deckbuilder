package com.example.mtg_deckbuilder.controller;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.repository.CardRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class DashboardController {

    private final CardRepository cardRepository;

    public DashboardController(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "mtg-dashboard";
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        List<Card> results = cardRepository.findByCardsBySubstring(query);
        model.addAttribute("results", results);
        model.addAttribute("query", query);
        return "search-results";
    }
}
