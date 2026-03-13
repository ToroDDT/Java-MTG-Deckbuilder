package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.model.DeckLayout;
import com.example.mtg_deckbuilder.service.ScryfallLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DeckBuilderController {

    private final ScryfallLibraryService scryfallLibraryService;

    @Autowired
    public DeckBuilderController(ScryfallLibraryService scryfallLibraryService) {
        this.scryfallLibraryService = scryfallLibraryService;
    }
    @GetMapping("/deck-builder")
    public String deckBuilderPage(Model model) {

        List<Card> listOfCommanders = scryfallLibraryService.findAllLegalCommanders();

        model.addAttribute("listOfCommanders", listOfCommanders);
        model.addAttribute("newDeckForm", new DeckLayout());
        model.addAttribute("availableTags", List.of());
        return "deck-builder";
    }
}



