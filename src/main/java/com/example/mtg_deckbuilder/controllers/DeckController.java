package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.CardService;
import com.example.mtg_deckbuilder.service.DeckService;
import com.example.mtg_deckbuilder.utils.DeckUtils;
import com.example.mtg_deckbuilder.utils.DeckSearchCriteria;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Controller
public class DeckController {

    private final CardService cardService;
    private final DeckService deckService;

    public DeckController(CardService cardService, DeckService deckService) {
        this.cardService = cardService;
        this.deckService = deckService;
    }

    @GetMapping("/collection")
    public String getDecks(@ModelAttribute("newDeck") NewDeck newDeck, @ModelAttribute("deckSearchCriteria") DeckSearchCriteria deckSearchCriteria, Model model, @AuthenticationPrincipal CustomUserDetails user) {

        Map<Deck, List<String>> decks = deckService.getAllDecksForUser(user.getId(), deckSearchCriteria);

        model.addAttribute("decks", decks);
        model.addAttribute("deckSearchCriteria", deckSearchCriteria);
        model.addAttribute("listOfCommanders", cardService.findAllLegalCommanders() );
        model.addAttribute("newDeck", newDeck);

        return "decks";
    }

    @PostMapping("/add-deck")
    public String addCardToDeck(@Valid @ModelAttribute("newDeck") NewDeck newDeck, Model model, @AuthenticationPrincipal CustomUserDetails user) {
        Optional<Card> card = cardService.findColorIdentity(newDeck.getCommander());
        card.ifPresent(value -> {
            newDeck.setColorIdentity(value.colorIdentity());
        });
        newDeck.setLastUpdate( LocalDate.now());
        newDeck.setUrl("/mtg-dashboard" + "/" + newDeck.getId());
        newDeck.setUserId(user.getId());
        deckService.addDeck(newDeck);
        return "redirect:/decks";
    }
}
