package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.repository.CardLibrary;
import com.example.mtg_deckbuilder.repository.ScryfallRepository;
import com.example.mtg_deckbuilder.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
public class DeckCardsController {

    private final ScryfallRepository scryfallRepository;
    private final CardLibrary cardLibrary;
    private final CardService cardService;

    @Autowired
    public DeckCardsController(CardLibrary cardLibrary, ScryfallRepository scryfallRepository, CardService cardService) {
        this.scryfallRepository = scryfallRepository;
        this.cardLibrary = cardLibrary;
        this.cardService = cardService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/collection/deck/{id}")
    public String deck(@PathVariable UUID id, Model model) throws IOException {
        model.addAttribute("cards", cardLibrary.getCardLibrary());
        System.out.println(id);
        return "mtg-dashboard";
    }

    @PostMapping("/collection/deck/{id}")
    public String addCardToDeck(@PathVariable UUID id, Model model) throws IOException {
        return "mtg-dashboard";
    }

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        List<Card> results = scryfallRepository.findByCardsBySubstring(query);
        model.addAttribute("results", results);
        model.addAttribute("query", query);
        return "search-results";
    }

    @GetMapping("/mtg-dashboard")
    public String getPlayerProfile(Model model) throws IOException {
        model.addAttribute("cards", cardLibrary.getCardLibrary());
        return "mtg-dashboard";
    }

    @PostMapping("/add-card")
    public String addCardToDeck(Model model) {
        return "mtg-dashboard";
    }
}
