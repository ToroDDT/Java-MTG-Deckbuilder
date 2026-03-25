package com.example.mtg_deckbuilder.service;

import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.repository.PersonalLibraryRepository;

import java.util.List;
import java.util.UUID;

public class DefaultPersonalLibraryService implements PersonalLibraryService {
    private final PersonalLibraryRepository personalLibraryRepository;

    public DefaultPersonalLibraryService(PersonalLibraryRepository personalLibraryRepository) {
        this.personalLibraryRepository = personalLibraryRepository;
    }
    @Override
    public void addCardToPersonalLibrary(OwnedCard ownedCard) {
        personalLibraryRepository.addCardToPersonalLibrary(ownedCard);
    }

    @Override
    public List<OwnedCard> getCardsFromPersonalLibrary(UUID userId) {
        return List.of();
    }
}
