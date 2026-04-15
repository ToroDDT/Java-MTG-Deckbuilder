package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.impl.ComboServiceImpl;
import com.example.mtg_deckbuilder.views.ComboViewModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class CombosController {
    private final ComboServiceImpl comboServiceImpl;
    public CombosController(ComboServiceImpl comboServiceImpl) {
        this.comboServiceImpl = comboServiceImpl;
    }
    @GetMapping("/personal-library/combos")
    public String combos(Model model) {
        ComboViewModel cardBrowserViewModel = new ComboViewModel();

        model.addAttribute("personalLibrary", cardBrowserViewModel);
        model.addAttribute("ownedCard", new OwnedCard());
        model.addAttribute("filters", new LibraryFilters());
        return "combo-browser";
    }

    @GetMapping(path = "/personal-library/combos-list", headers = "hx-request=true")
    public  String getCombos(@ModelAttribute("personalLibraryFilters") LibraryFilters personalLibraryFilters, Model model, @AuthenticationPrincipal CustomUserDetails user) throws Exception {

        ComboViewModel cardBrowserViewModel = new ComboViewModel();
        var combosList = comboServiceImpl.findCombos(user, personalLibraryFilters);

        model.addAttribute("personalLibrary", cardBrowserViewModel);
        model.addAttribute("ownedCard", new OwnedCard());
        model.addAttribute("filters", new LibraryFilters());
        model.addAttribute("cardCombos", combosList );
        return "combos :: combos-section";
    }
}
