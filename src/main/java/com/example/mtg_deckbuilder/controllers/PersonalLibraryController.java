package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.model.PersonalLibraryFilters;
import com.example.mtg_deckbuilder.model.SortOptions;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.DefaultPersonalLibraryService;
import com.example.mtg_deckbuilder.service.PersonalLibraryService;
import com.example.mtg_deckbuilder.views.LibraryViewModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class PersonalLibraryController {

    private final PersonalLibraryService personalLibraryService;

    private  final List<String> allColors = List.of("W", "U", "B", "R", "G");

    PersonalLibraryController(DefaultPersonalLibraryService personalLibraryService) {
        this.personalLibraryService = personalLibraryService;
    }

    @GetMapping("/personal-library")
    public String getPersonalLibrary(Model model, @AuthenticationPrincipal CustomUserDetails user) {
        LibraryViewModel libraryView = personalLibraryService.buildPersonalLibraryViewModel(user);
        model.addAttribute("personalLibrary", libraryView);
        model.addAttribute("ownedCard", new OwnedCard());
        model.addAttribute("filters", new PersonalLibraryFilters());

        return "personal-library";
    }
    @PostMapping("/personal-library/add")
    public String addCardToPersonalLibrary(@ModelAttribute("ownedCard") OwnedCard ownedCard, @AuthenticationPrincipal CustomUserDetails user) {
        personalLibraryService.addCardToPersonalLibrary(ownedCard, user.getId());
        return "redirect:/personal-library";
    }

    @GetMapping(path = "/personal-library/search", headers = "hx-request=true")
    public  String getCardsMatchingFilter(@ModelAttribute("personalLibraryFilters") PersonalLibraryFilters personalLibraryFilters, Model model, @AuthenticationPrincipal CustomUserDetails user){
         var cards = personalLibraryService.getCardsFromPersonalLibrary(user.getId(), personalLibraryFilters);
        model.addAttribute("cards", cards);
        model.addAttribute("ownedCard", new OwnedCard());
        model.addAttribute("personalLibraryFilters", new PersonalLibraryFilters());
        model.addAttribute("allColors", allColors);
        model.addAttribute("sortOptions", SortOptions.values());
        return "fragments/personal-cards :: personal-cards";
    }
}