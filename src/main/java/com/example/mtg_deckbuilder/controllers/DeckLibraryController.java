package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.ScryfallLibraryService;
import com.example.mtg_deckbuilder.service.DeckService;
import com.example.mtg_deckbuilder.utils.DeckSearchCriteria;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Controller
public class DeckLibraryController {

    private final ScryfallLibraryService scryfallLibraryService;
    private final DeckService deckService;

    public DeckLibraryController(ScryfallLibraryService scryfallLibraryService, DeckService deckService) {
        this.scryfallLibraryService = scryfallLibraryService;
        this.deckService = deckService;
    }

    @GetMapping("/collection")
    public String getDecks(@ModelAttribute("newDeck") NewDeck newDeck, @ModelAttribute("deckSearchCriteria") DeckSearchCriteria deckSearchCriteria, Model model, @AuthenticationPrincipal CustomUserDetails user) {
        model.addAllAttributes(Map.of(
                "decks", deckService.getAllDecksForUser(user.getId(), deckSearchCriteria),
                "deckSearchCriteria", deckSearchCriteria,
                "listOfCommanders", scryfallLibraryService.findAllLegalCommanders(),
                "newDeck", newDeck
        ));
        return "decks";
    }

    @GetMapping(path = "/search", headers = "hx-request=true")
    String getAllDecksMatchingSearch(@ModelAttribute("newDeck") NewDeck newDeck, @ModelAttribute("deckSearchCriteria") DeckSearchCriteria deckSearchCriteria, Model model, @AuthenticationPrincipal CustomUserDetails user) {
        System.out.println("THis is working");
        model.addAllAttributes(Map.of(
                "decks", deckService.getAllDecksForUser(user.getId(), deckSearchCriteria)
        ));
        return "fragments/deck-search-results :: decks";
    }

    @PostMapping("/add-deck")
    public String addCardToDeck(@Valid @ModelAttribute("newDeck") NewDeck newDeck, @RequestParam("file") MultipartFile file, @AuthenticationPrincipal CustomUserDetails user) throws IOException {
    try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

        reader.lines().forEach(line -> {
            System.out.println(line);
        });
    }

        newDeck.setLastUpdate( LocalDate.now());
        newDeck.setUrl("/mtg-dashboard" + "/" + newDeck.getId());
        newDeck.setUserId(user.getId());
        deckService.addDeck(newDeck);
        return "redirect:/decks";
    }
}
