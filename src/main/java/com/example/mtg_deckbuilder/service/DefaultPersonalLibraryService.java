package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.exceptions.CardDoesNotExistException;
import com.example.mtg_deckbuilder.model.*;
import com.example.mtg_deckbuilder.repository.DefaultPersonalLibraryRepository;
import com.example.mtg_deckbuilder.repository.PersonalLibraryRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.utils.CardUtils;
import com.example.mtg_deckbuilder.views.LibraryViewModel;
import org.springframework.stereotype.Service;

import javax.swing.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
        return personalLibraryRepository
                .getAllPersonalLibraryCardsForUser(userId)
                .stream()
                .peek(ownedCard -> {
                    if (ownedCard.getTags() == null || ownedCard.getTags().isEmpty()) {
                        ownedCard.setTags(List.of());
                    }
                })
                .toList();
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
                .peek(ownedCard -> {
                    if (ownedCard.getTags() == null || ownedCard.getTags().isEmpty()) {
                        ownedCard.setTags(List.of());
                    }
                })
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
                    case NAME_DESC-> Comparator.comparing((OwnedCard ownedCard) ->
                                    ownedCard
                                            .getCard()
                                            .getName())
                            .reversed();
                    case NAME_ASC-> Comparator.comparing((OwnedCard ownedCard) ->
                            ownedCard
                                    .getCard()
                                    .getName()
                    );
                    default -> Comparator.comparing((OwnedCard ownedCard) ->
                            ownedCard
                                    .getCard()
                                    .getName()
                    ); // or whatever your default sort is
                })
                .toList();
    }
    @Override
    public Map<ColorIdentity, Long> getAmountOfEachColorIdentity(UUID userId) {
        return personalLibraryRepository.getAllPersonalLibraryCardsForUser(userId)
                .stream()
                .collect(Collectors.groupingBy(ColorIdentity::fromString,  Collectors.counting()));
    }

    @Override
    public LibraryViewModel buildPersonalLibraryViewModel(CustomUserDetails userId) {
        var cards = getCardsFromPersonalLibrary(userId.getId());

        // Calculate total value
        var total = getTotalValue(cards);
        var colorCounts = getColorCount(userId);

        // Use the Builder to assemble the object
        return LibraryViewModel.builder()
                .cards(cards)
                .totalCards(cards.size())
                .totalValue(total)
                .avgPrice(cards.isEmpty() ? 0.0 : total / cards.size())
                .colorIdentityAmounts(colorCounts)
                .build();
    }

    @Override
    public LibraryViewModel buildPersonalLibraryViewModel(CustomUserDetails userId, PersonalLibraryFilters personalLibraryFilters) {
        var cards = this.getCardsFromPersonalLibrary(userId.getId(), personalLibraryFilters);


        var total = getTotalValue(cards);
        var colorCounts = getColorCount(userId);


        // Use the Builder to assemble the object
        return LibraryViewModel.builder()
                .cards(cards)
                .totalCards(cards.size())
                .totalValue(total)
                .avgPrice(cards.isEmpty() ? 0.0 : total / cards.size())
                .colorIdentityAmounts(colorCounts)
                .build();
    }

    private Double getTotalValue(List<OwnedCard> cards) {
        // Calculate total value
        return cards.stream()
                .filter(c -> c.getCard() != null && c.getCard().getPrices().getUsd() != null)
                .mapToDouble(c -> c.getCard().getPrices().getUsd())
                .sum();
    }

    private Map<String, Long> getColorCount(CustomUserDetails userId) {
        // Format color counts
        Map<String, Long> colorCounts = new HashMap<>();
        getAmountOfEachColorIdentity(userId.getId())
                .forEach((key, value) -> colorCounts.put(key.name(), value));
        return colorCounts;
    }
}
