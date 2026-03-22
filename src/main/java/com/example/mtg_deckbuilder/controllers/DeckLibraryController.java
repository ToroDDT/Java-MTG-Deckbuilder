package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.ScryfallLibraryService;
import com.example.mtg_deckbuilder.service.DefaultDeckService;
import com.example.mtg_deckbuilder.utils.DeckSearchCriteria;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
    private final DefaultDeckService defaultDeckService;

    public DeckLibraryController(ScryfallLibraryService scryfallLibraryService, DefaultDeckService defaultDeckService) {
        this.scryfallLibraryService = scryfallLibraryService;
        this.defaultDeckService = defaultDeckService;
    }

    @GetMapping("/collection")
    public String getDecks(@ModelAttribute("newDeck") NewDeck newDeck, @ModelAttribute("deckSearchCriteria") DeckSearchCriteria deckSearchCriteria, Model model, @AuthenticationPrincipal CustomUserDetails user) {
        model.addAllAttributes(Map.of(
                "decks", defaultDeckService.getAllDecksForUser(user.getId(), deckSearchCriteria),
                "deckSearchCriteria", deckSearchCriteria,
                "listOfCommanders", scryfallLibraryService.findAllLegalCommanders(),
                "newDeck", newDeck
        ));
        return "decks";
    }

    @GetMapping(path = "/search", headers = "hx-request=true")
    String getAllDecksMatchingSearch(@ModelAttribute("newDeck") NewDeck newDeck, @ModelAttribute("deckSearchCriteria") DeckSearchCriteria deckSearchCriteria, Model model, @AuthenticationPrincipal CustomUserDetails user) {
        model.addAllAttributes(Map.of(
                "decks", defaultDeckService.getAllDecksForUser(user.getId(), deckSearchCriteria)
        ));
        return "fragments/deck-search-results :: decks";
    }

    @PostMapping(value = "/add-deck")
    public String addCardToDeck(
            @Valid @ModelAttribute("newDeck") NewDeck newDeck,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails user) throws IOException {

        if (file != null && !file.isEmpty()) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

                reader.lines().forEach(System.out::println);
            }
        }

        scryfallLibraryService.findByName(newDeck.getCommander())
                .ifPresent(card -> {
                    newDeck.setImage(card.getImage());
                    newDeck.setColorIdentity(card.getColorIdentity().toString());
                    newDeck.setLastUpdate(LocalDate.now());
                    newDeck.setUrl("/mtg-dashboard/" + newDeck.getId());
                    newDeck.setUserId(user.getId());
                });

        defaultDeckService.addDeck(newDeck);

        return "redirect:/collection";
    }


}