package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.repository.CardNameRepository;
import com.example.mtg_deckbuilder.repository.MtgJsonRepository;
import com.example.mtg_deckbuilder.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
public class DashboardController {

    private final MtgJsonRepository mtgJsonRepository;
    private final CardNameRepository cardNameRepository;
    private final CardService cardService;

    @Autowired
    public DashboardController(MtgJsonRepository mtgJsonRepository, CardNameRepository cardNameRepository, CardService cardService) {
        this.mtgJsonRepository = mtgJsonRepository;
        this.cardNameRepository = cardNameRepository;
        this.cardService = cardService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) throws IOException {
        model.addAttribute("cards", cardNameRepository.readFile());
        return "mtg-dashboard";
    }


    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        List<Card> results = mtgJsonRepository.findByCardsBySubstring(query);
        model.addAttribute("results", results);
        model.addAttribute("query", query);
        return "search-results";
    }

    @GetMapping("/mtg-dashboard")
    public String getPlayerProfile(Model model) throws IOException {
        model.addAttribute("cards", cardNameRepository.readFile());
        return "mtg-dashboard";
    }

    @PostMapping("/add-card")
    public String addCardToDeck(Model model) {
        return "mtg-dashboard";
    }
}
