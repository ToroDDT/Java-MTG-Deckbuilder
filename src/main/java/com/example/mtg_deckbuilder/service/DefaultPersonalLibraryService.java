package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.exceptions.CardDoesNotExistException;
import com.example.mtg_deckbuilder.model.*;
import com.example.mtg_deckbuilder.repository.DefaultPersonalLibraryRepository;
import com.example.mtg_deckbuilder.repository.PersonalLibraryRepository;
import com.example.mtg_deckbuilder.utils.CardUtils;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class DefaultPersonalLibraryService implements PersonalLibraryService {
    private final PersonalLibraryRepository personalLibraryRepository;
    private final ScryfallLibraryService scryfallLibraryService;

    public DefaultPersonalLibraryService(DefaultPersonalLibraryRepository personalLibraryRepository, ScryfallLibraryService scryfallLibraryService) {
        this.personalLibraryRepository = personalLibraryRepository;
        this.scryfallLibraryService = scryfallLibraryService;
    }
    @Override
    public void addCardToPersonalLibrary(OwnedCard ownedCard, UUID user) throws CardDoesNotExistException{
        var card = scryfallLibraryService.findByName(ownedCard.getName());
        if (card.isPresent()) {
            ownedCard.setId(card.get().getId());
            ownedCard.setCardId(card.get().getId());
            ownedCard.setTags(List.of());
            ownedCard.setImage(card.get().getImage());
            ownedCard.setUserId(user);
            ownedCard.setUpdatedAt(LocalDate.now());
            ownedCard.setDateAdded(LocalDate.now());
            personalLibraryRepository.addCardToPersonalLibrary(ownedCard);
        } else {
            throw new CardDoesNotExistException(ownedCard.getName());
        }
    }

    @Override
    public List<OwnedCard> getCardsFromPersonalLibrary(UUID userId) {
        return personalLibraryRepository.getAllPersonalLibraryCardsForUser(userId);
    }
    @Override
    public List<OwnedCard> getCardsFromPersonalLibrary(UUID userid, PersonalLibraryFilters personalLibraryFilters) {

        var cardType = CardType.fromString(personalLibraryFilters.getCardType());
        SortOptions sortBy = personalLibraryFilters.getSortBy();

        return personalLibraryRepository.getAllPersonalLibraryCardsForUser(userid).stream()
                .filter(card -> CardUtils.matchesSearchQuery(card, personalLibraryFilters.getCardName()))
                .filter(card -> CardUtils.matchesSelectedColors(card, personalLibraryFilters.getSelectedColors()))
                .filter(card -> CardUtils.matchesSelectedType(card, cardType))
                .filter(card -> CardUtils.matchesCmcRange(card, personalLibraryFilters))
                .sorted(switch (sortBy) {
                    case PRICE_ASC -> Comparator.comparing(
                            (OwnedCard ownedCard) -> ownedCard.getCard().getPrices().getUsd(),
                            Comparator.nullsLast(Comparator.naturalOrder())
                    );

                    case PRICE_DESC -> Comparator.comparing(
                            (OwnedCard ownedCard) -> ownedCard.getCard().getPrices().getUsd(),
                            Comparator.nullsLast(Comparator.reverseOrder())
                    );                   case CMC_ASC-> Comparator.comparing(ownedCard ->
                            ownedCard
                                    .getCard()
                                    .getCmc());
                    case CMC_DESC-> Comparator.comparing((OwnedCard ownedCard) ->
                                    ownedCard
                                            .getCard()
                                            .getCmc())
                            .reversed();
                    case NAME_DESC-> Comparator.comparing(OwnedCard::getName).reversed();
                    default -> Comparator.comparing(OwnedCard::getName); // or whatever your default sort is
                })
                .toList();
    }
}
