package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.NewDeckForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DeckBuilderController {
    @GetMapping("/deck-builder")   // or whatever route loads deck-builder.html
    public String deckBuilderPage(Model model) {
        model.addAttribute("newDeckForm", new NewDeckForm());
        model.addAttribute("availableTags", List.of());   // replace with real data
        return "deck-builder";
    }
}



