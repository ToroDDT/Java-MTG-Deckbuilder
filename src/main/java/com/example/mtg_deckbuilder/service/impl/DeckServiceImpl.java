package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.cache.UserDecksCache;
import com.example.mtg_deckbuilder.exceptions.DeckDoesNotExistException;
import com.example.mtg_deckbuilder.model.CardEntry;
import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.repository.api.DeckRepository;
import com.example.mtg_deckbuilder.repository.api.PersonalLibraryRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.CardService;
import com.example.mtg_deckbuilder.service.api.DeckService;
import com.example.mtg_deckbuilder.subscribers.LibraryUpdatedEvent;
import com.example.mtg_deckbuilder.utils.DeckSearchCriteria;
import com.example.mtg_deckbuilder.utils.DeckUtils;
import com.example.mtg_deckbuilder.views.api.DeckListItemView;
import com.example.mtg_deckbuilder.views.impl.DeckListItemViewImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class DeckServiceImpl implements DeckService {

    private final DeckRepository deckRepository;
    private final UserDecksCache userDecksCache;
    private final PersonalLibraryRepository personalLibraryRepository;
    private final ApplicationEventPublisher publisher;
    private final CardService cardService;


    @Autowired
    public DeckServiceImpl(DeckRepository deckRepository, UserDecksCache userDecksCache,
                           PersonalLibraryRepository personalLibraryRepository,
                           ApplicationEventPublisher publisher,
                           CardService cardService) {
        this.deckRepository = deckRepository;
        this.userDecksCache = userDecksCache;
        this.personalLibraryRepository = personalLibraryRepository;
        this.publisher = publisher;
        this.cardService = cardService;
    }


    @Override
    public void addDeck(NewDeck newDeck) {
        deckRepository.createNewDeckEntry(newDeck);
        userDecksCache.evictForUser(newDeck.getUserId());
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
                publisher.publishEvent(new LibraryUpdatedEvent(this, user));
                return ownedDeck.name();
            }
        }

        throw new DeckDoesNotExistException(deck);
    }

    @Override
    public void addCard(CustomUserDetails user, String deck, String cardId) {
        var owned = personalLibraryRepository.findCardExists(user.getId(), cardId);
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
        publisher.publishEvent(new LibraryUpdatedEvent(this, user));
    }

    @Override
    public void removeDeckEntry(CustomUserDetails user, UUID deckId, UUID deckEntryId) {
        deckRepository.removeDeckEntry(user, deckId, deckEntryId);
    }

    @Override
    public List<DeckListItemView> getDecks(CustomUserDetails user, DeckSearchCriteria deckSearchCriteria) {
        var deckPrices = deckRepository.getDeckTotalPricesForUser(user.getId());

        var decks = deckRepository.getDecks(user).stream()
                .filter(deck -> DeckUtils.matchesSearchQuery(deck, deckSearchCriteria.getSearchQuery()))
                .filter(deck -> DeckUtils.matchesSelectedColors(deck, deckSearchCriteria.getSelectedColors()))
                .filter(deck -> DeckUtils.matchesFolder(deck, deckSearchCriteria.getFolder()))
                .toList();

        var colorIdentityForEachDeck = DeckUtils.getColorIdentityOfDecks(decks);

        List<DeckListItemView> items = IntStream.range(0, decks.size())
                .mapToObj(i -> (DeckListItemView) new DeckListItemViewImpl(
                        decks.get(i),
                        colorIdentityForEachDeck.get(i),
                        deckPrices.getOrDefault(decks.get(i).id(), 0.0)))
                .toList();

        Comparator<DeckListItemView> comparator = switch (deckSearchCriteria.getSortBy()) {
            case "commander" -> Comparator.comparing(item -> item.deck().commander(), Comparator.nullsLast(String::compareToIgnoreCase));
            case "color identity" -> Comparator.comparing(item -> item.deck().colors_identity(), Comparator.nullsLast(String::compareToIgnoreCase));
            case "price" -> Comparator.comparing(DeckListItemView::totalPrice);
            case "lastUpdated", "lastUpdate" -> Comparator.comparing(item -> item.deck().last_updated(), Comparator.nullsLast(LocalDate::compareTo));
            default -> Comparator.comparing(item -> item.deck().last_updated(), Comparator.nullsLast(LocalDate::compareTo));
        };

        if ("asc".equalsIgnoreCase(deckSearchCriteria.getSortOrder())) {
            return items.stream().sorted(comparator).toList();
        }
        return items.stream().sorted(comparator.reversed()).toList();
    }

    @Override
    public void updateDeck(CustomUserDetails user, UUID deckId, String name, String commander) {
        Deck existing = userDecksCache.getAllDecksForUser(user).stream()
                .filter(deck -> deck.id().equals(deckId))
                .findFirst()
                .orElseThrow(() -> new DeckDoesNotExistException(deckId.toString()));

        String commanderName = commander != null && !commander.isBlank() ? commander : existing.commander();
        String colorIdentity = existing.colors_identity();
        String image = existing.image();

        var commanderCard = cardService.findByName(commanderName);
        if (commanderCard.isPresent()) {
            var card = commanderCard.get();
            colorIdentity = card.getColorIdentity().toString();
            image = card.getImage();
        }

        deckRepository.updateDeckMetadata(
                user,
                deckId,
                name,
                commanderName,
                colorIdentity,
                image,
                LocalDate.now());
        userDecksCache.evictForUser(user.getId());
    }


    @Override
    public List<Deck> getDeckIds(CustomUserDetails user) {
        return userDecksCache.getDeckIdsForUser(user);
    }

}
