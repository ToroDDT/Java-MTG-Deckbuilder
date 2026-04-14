package com.example.mtg_deckbuilder.controllers;

import com.example.mtg_deckbuilder.exceptions.CardDoesNotExistException;
import com.example.mtg_deckbuilder.model.AddCardToDeckRequest;
import com.example.mtg_deckbuilder.repository.impl.CardRepositoryImpl;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.impl.DeckServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.UUID;

@Controller
public class DeckController {

    private final CardRepositoryImpl scryfallRepository;
    private final CardLibrary cardLibrary;
    private final DeckServiceImpl deckServiceImpl;

    @Autowired
    public DeckController(DeckServiceImpl deckServiceImpl, CardLibrary cardLibrary, CardRepositoryImpl scryfallRepository) {
        this.scryfallRepository = scryfallRepository;
        this.cardLibrary = cardLibrary;
        this.deckServiceImpl = deckServiceImpl;
    }

    @GetMapping("/collection/deck/{id}")
    public String deck(@PathVariable UUID id, Model model) throws IOException {
        model.addAttribute("cards", cardLibrary.getCardLibrary());
        model.addAttribute("deckId", id);
        return "mtg-dashboard";
    }

    @PostMapping("/collection/add-card")
    public String addCardToDeck(@RequestParam String cardName, @RequestParam UUID deckId,
                                 @AuthenticationPrincipal CustomUserDetails user) {
        var card = scryfallRepository.findByName(cardName).orElseThrow(() -> new CardDoesNotExistException(cardName));

        var cardRequest = new AddCardToDeckRequest(deckId, user.getId(), card.getId(), false, null);
        deckServiceImpl.addCardToDeck(cardRequest);
        return "mtg-dashboard";
    }
}
