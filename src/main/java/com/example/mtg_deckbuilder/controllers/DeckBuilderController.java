package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.model.DeckLayout;
import com.example.mtg_deckbuilder.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DeckBuilderController {

    private final CardService cardService;

    @Autowired
    public DeckBuilderController(CardService cardService) {
        this.cardService = cardService;
    }
    @GetMapping("/deck-builder")
    public String deckBuilderPage(Model model) {
        List<Card> listOfCommanders = cardService.findAllLegalCommanders();
        for (Card card : listOfCommanders) {
            System.out.println(card.name());
        }
        model.addAttribute("listOfCommanders", listOfCommanders);
        model.addAttribute("newDeckForm", new DeckLayout());
        model.addAttribute("availableTags", List.of());
        return "deck-builder";
    }
}



