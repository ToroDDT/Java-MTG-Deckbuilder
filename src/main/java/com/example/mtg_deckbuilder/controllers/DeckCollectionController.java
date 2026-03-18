package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.ScryfallLibraryService;
import com.example.mtg_deckbuilder.service.DeckService;
import com.example.mtg_deckbuilder.utils.DeckSearchCriteria;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
public class DeckCollectionController {

    private final ScryfallLibraryService scryfallLibraryService;
    private final DeckService deckService;

    public DeckCollectionController(ScryfallLibraryService scryfallLibraryService, DeckService deckService) {
        this.scryfallLibraryService = scryfallLibraryService;
        this.deckService = deckService;
    }

    @GetMapping("/collection")
    public String getDecks(@ModelAttribute("newDeck") NewDeck newDeck, @ModelAttribute("deckSearchCriteria") DeckSearchCriteria deckSearchCriteria, Model model, @AuthenticationPrincipal CustomUserDetails user, HttpServletResponse response) {
        model.addAllAttributes(Map.of(
                "decks", deckService.getAllDecksForUser(user.getId(), deckSearchCriteria),
                "deckSearchCriteria", deckSearchCriteria,
                "listOfCommanders", scryfallLibraryService.findAllLegalCommanders(),
                "newDeck", newDeck
        ));
        response.setHeader("Cache-Control", "max-age=" + TimeUnit.DAYS.toHours(5));
        return "decks";
    }

    @GetMapping(path = "/search", headers = "hx-request=true")
    String getAllDecksMatchingSearch(@ModelAttribute("newDeck") NewDeck newDeck, @ModelAttribute("deckSearchCriteria") DeckSearchCriteria deckSearchCriteria, Model model, @AuthenticationPrincipal CustomUserDetails user) {
        System.out.println("THis is working");
        model.addAllAttributes(Map.of(
                "decks", deckService.getAllDecksForUser(user.getId(), deckSearchCriteria)
        ));
        return "fragments/deck-search-results :: #search-results";
    }

    @PostMapping("/add-deck")
    public String addCardToDeck(@Valid @ModelAttribute("newDeck") NewDeck newDeck, Model model, @AuthenticationPrincipal CustomUserDetails user) {
        Optional<Card> card = scryfallLibraryService.findColorIdentity(newDeck.getCommander());

        newDeck.setLastUpdate( LocalDate.now());
        newDeck.setUrl("/mtg-dashboard" + "/" + newDeck.getId());
        newDeck.setUserId(user.getId());
        deckService.addDeck(newDeck);
        return "redirect:/decks";
    }
}
