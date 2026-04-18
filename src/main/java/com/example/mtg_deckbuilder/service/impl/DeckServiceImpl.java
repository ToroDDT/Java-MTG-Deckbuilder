package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.cache.UserDecksCache;
import com.example.mtg_deckbuilder.exceptions.DeckDoesNotExistException;
import com.example.mtg_deckbuilder.model.CardEntry;
import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.repository.api.DeckRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.CardService;
import com.example.mtg_deckbuilder.service.api.DeckService;
import com.example.mtg_deckbuilder.utils.DeckSearchCriteria;
import com.example.mtg_deckbuilder.utils.DeckUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.IntStream;

@Service
public class DeckServiceImpl implements DeckService {

    private final DeckRepository deckRepository;
    private final UserDecksCache userDecksCache;
    private final CardService cardService;


    @Autowired
    public DeckServiceImpl(DeckRepository deckRepository, UserDecksCache userDecksCache, CardService cardService) {
        this.deckRepository = deckRepository;
        this.userDecksCache = userDecksCache;
        this.cardService = cardService;
    }


    @Override
    public void addDeck(NewDeck newDeck) {

    }

    @Override
    public void addCard(CardEntry cardEntry) {

        userDecksCache.getAllDecksForUser(cardEntry.userId()).stream()
                .filter(deck -> deck.id().equals(cardEntry.deckId()))
                .findFirst()
                .orElseThrow(() -> new DeckDoesNotExistException(cardEntry.personalLibraryCardId().toString()));

        deckRepository.addCard(cardEntry);
    }

    @Override
    public String addCard(CustomUserDetails user, String deck, UUID cardId, UUID personalLibraryCardId) {
        var decks = userDecksCache.getAllDecksForUser(user);

        for (Deck ownedDeck : decks) {
            if (Objects.equals(ownedDeck.name(), deck)) {
                addCard(new CardEntry(ownedDeck.id(), user, cardId, false, personalLibraryCardId));
                return ownedDeck.name();
            }
        }

        throw new DeckDoesNotExistException(deck);
    }

    @Override
    public Map<Deck, List<String>> getDecks(CustomUserDetails user, DeckSearchCriteria deckSearchCriteria) {
        Map<Deck, List<String>> finalDecks = new LinkedHashMap<>();

        var decks = deckRepository.getDecks(user).stream()
                .filter(deck -> DeckUtils.matchesSearchQuery(deck, deckSearchCriteria.getSearchQuery()))
                .filter(deck -> DeckUtils.matchesSelectedColors(deck, deckSearchCriteria.getSelectedColors()))
                .filter(deck -> DeckUtils.matchesFolder(deck, deckSearchCriteria.getFolder()))
                .sorted(switch (deckSearchCriteria.getSortBy()) {
                    case "commander" -> Comparator.comparing(Deck::commander);
                    case "color identity" -> Comparator.comparing(Deck::colors_identity);
                    default -> Comparator.comparing(Deck::last_updated); // or whatever your default sort is
                })
                .toList();

        var colorIdentityForEachDeck = DeckUtils.getColorIdentityOfDecks(decks);

        IntStream.range(0, decks.size())
                .forEach(i -> finalDecks.put(decks.get(i), colorIdentityForEachDeck.get(i)));
        return finalDecks;
    }
    @Override
    public List<Deck> getDecks(CustomUserDetails user) {
        return deckRepository.getDecks(user);
    }

    @Override
    public List<String> getDeckNames(CustomUserDetails user) {
        return deckRepository.getDeckNames(user);
    }

}