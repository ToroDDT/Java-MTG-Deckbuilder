package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.exceptions.CardDoesNotExistException;
import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.repository.CardLibrary;
import com.example.mtg_deckbuilder.repository.ScryfallRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.DeckService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Controller
public class DeckCardsController {

    private final ScryfallRepository scryfallRepository;
    private final CardLibrary cardLibrary;
    private final DeckService deckService;

    @Autowired
    public DeckCardsController(DeckService deckService, CardLibrary cardLibrary, ScryfallRepository scryfallRepository) {
        this.scryfallRepository = scryfallRepository;
        this.cardLibrary = cardLibrary;
        this.deckService = deckService;
    }

    @GetMapping("/")
    public String index(HttpServletResponse response) {
        response.setHeader("Cache-Control", "max-age=" + TimeUnit.DAYS.toSeconds(30));
        return "index";
    }

    @GetMapping("/collection/deck/{id}")
    public String deck(@PathVariable UUID id, Model model) throws IOException {
        model.addAttribute("cards", cardLibrary.getCardLibrary());
        model.addAttribute("deckId", id);
        return "mtg-dashboard";
    }

    @PostMapping("/collection/add-card")
    public String addCardToDeck(@RequestParam String cardName, @RequestParam UUID deckId,
                                Model model, @AuthenticationPrincipal CustomUserDetails user) throws IOException {
        var card = scryfallRepository.findByName(cardName);
        var cardId = card.map(Card::getId).orElseThrow(() -> new CardDoesNotExistException(cardName));
        deckService.addCardToDeck(deckId, user.getId(), cardId, false, null );
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
