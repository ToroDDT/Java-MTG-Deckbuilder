package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.views.ComboViewModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CombosController {
    @GetMapping("/personal-library/combos")
    public String combos(Model model) {
        ComboViewModel cardBrowserViewModel = new ComboViewModel();

        model.addAttribute("personalLibrary", cardBrowserViewModel);
        model.addAttribute("ownedCard", new OwnedCard());
        model.addAttribute("filters", new LibraryFilters());
        return "combo-browser";
    }
}
