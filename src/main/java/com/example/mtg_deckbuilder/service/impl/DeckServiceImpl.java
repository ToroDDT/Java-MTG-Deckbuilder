package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.cache.UserDecksCache;
import com.example.mtg_deckbuilder.exceptions.DeckDoesNotExistException;
import com.example.mtg_deckbuilder.model.CardEntry;
import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.repository.api.DeckRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.DeckService;
import com.example.mtg_deckbuilder.service.api.PersonalLibraryService;
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
    private final PersonalLibraryService personalLibraryService;


    @Autowired
    public DeckServiceImpl(DeckRepository deckRepository, UserDecksCache userDecksCache, PersonalLibraryService personalLibraryService) {
        this.deckRepository = deckRepository;
        this.userDecksCache = userDecksCache;
        this.personalLibraryService = personalLibraryService;
    }


    @Override
    public void addDeck(NewDeck newDeck) {
        deckRepository.createNewDeckEntry(newDeck);
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
                var card = CardEntry.builder()
                        .deckId(ownedDeck.id())
                        .userId(user)
                        .cardId(cardId)
                        .isSideboard(false)
                        .personalLibraryCardId(personalLibraryCardId)
                        .build();

                addCard(card);
                return ownedDeck.name();
            }
        }

        throw new DeckDoesNotExistException(deck);
    }

    @Override
    public void addCard(CustomUserDetails user, String deck, String cardId) {
        var owned = personalLibraryService.findCard(user, cardId) == true;
        var card = CardEntry.builder()
                .deckId(UUID.fromString(deck))
                .cardId(UUID.fromString(cardId))
                .owned((owned))
                .userId(user)
                .build();
        deckRepository.addCard(card);
    }

    @Override
    public void removePersonalLibraryCardFromDeck(CustomUserDetails user, UUID personalLibraryCardId) {
        deckRepository.removeDeckEntryByPersonalLibraryCardId(user, personalLibraryCardId);
    }

    @Override
    public void removeDeckEntry(CustomUserDetails user, UUID deckId, UUID deckEntryId) {
        deckRepository.removeDeckEntry(user, deckId, deckEntryId);
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
    public List<Deck> getDeckIds(CustomUserDetails user) {
        return deckRepository.getDeckIds(user);
    }

}
