package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.service.CardService;
import com.example.mtg_deckbuilder.service.DeckService;
import com.example.mtg_deckbuilder.utils.DeckUtils;
import com.example.mtg_deckbuilder.utils.DeckSearchCriteria;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class DeckController {

    private final CardService cardService;
    private final DeckService deckService;

    public DeckController(CardService cardService, DeckService deckService) {
        this.cardService = cardService;
        this.deckService = deckService;
    }

    @GetMapping("/decks")
    public String getDecks(@ModelAttribute("newDeck") NewDeck newDeck, @ModelAttribute("deckSearchCriteria") DeckSearchCriteria deckSearchCriteria, Model model) {



        List<Deck> decks = new ArrayList<>();

        Deck deck1 = new Deck(1L, "Galadriel Light of Valinor", Arrays.asList("W", "U", "G"), "Commander", 2, LocalDate.of(2024, 7, 15), "/decks/1");
        decks.add(deck1);

        List<Deck> filteredDecks = DeckUtils.filterDecks(decks, deckSearchCriteria);

        filteredDecks = DeckUtils.sortDecks(filteredDecks, deckSearchCriteria.getSortBy(), deckSearchCriteria.getSortOrder());

        model.addAttribute("decks", filteredDecks);
        model.addAttribute("deckSearchCriteria", deckSearchCriteria);
        model.addAttribute("listOfCommanders", cardService.findAllLegalCommanders() );
        model.addAttribute("newDeck", newDeck);

        return "decks";
    }

    @PostMapping("/add-deck")
    public String addCardToDeck(@Valid @ModelAttribute("newDeck") NewDeck newDeck, Model model) {
        System.out.println("its workdsing ");
        deckService.addDeck(newDeck);
        return "redirect:/decks";
    }
}
