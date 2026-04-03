package com.example.mtg_deckbuilder.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PersonalLibraryController {
    @GetMapping("/personal-library")
    public String personalLibrary(){

        return "personal-library";
    }
    @PostMapping("/personal-library/add")
    public String addCardToPersonalLibrary(@ModelAttribute("ownedCard") OwnedCard ownedCard, @AuthenticationPrincipal CustomUserDetails user) {
        personalLibraryService.addCardToPersonalLibrary(ownedCard, user.getId());
        return "redirect:/personal-library";
    }
}