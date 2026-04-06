package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.exceptions.CardDoesNotExistException;
import com.example.mtg_deckbuilder.model.CardType;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.model.PersonalLibraryFilters;
import com.example.mtg_deckbuilder.repository.DefaultPersonalLibraryRepository;
import com.example.mtg_deckbuilder.repository.PersonalLibraryRepository;
import com.example.mtg_deckbuilder.utils.CardUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

        return personalLibraryRepository.getAllPersonalLibraryCardsForUser(userid).stream()
                .filter(card -> CardUtils.matchesSearchQuery(card, personalLibraryFilters.getCardName()))
                .filter(card -> CardUtils.matchesSelectedColors(card, personalLibraryFilters.getSelectedColors()))
                .filter(card -> CardUtils.matchesSelectedType(card, cardType))
                .filter(card -> CardUtils.matchesCmcRange(card, personalLibraryFilters))
                .toList();
    }
}
