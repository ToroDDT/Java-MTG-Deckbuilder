package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.repository.api.BuilderRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.BuilderService;
import com.example.mtg_deckbuilder.service.api.CardService;
import com.example.mtg_deckbuilder.service.api.DeckService;
import com.example.mtg_deckbuilder.service.api.PersonalLibraryService;
import com.example.mtg_deckbuilder.utils.DeckOptimizerV2;
import com.example.mtg_deckbuilder.views.api.BuilderCardHoverView;
import com.example.mtg_deckbuilder.views.api.BuilderCardQueryView;
import com.example.mtg_deckbuilder.views.api.BuilderDeckLayoutView;
import com.example.mtg_deckbuilder.views.api.BuilderDeckSection;
import com.example.mtg_deckbuilder.views.api.BuilderMainView;
import com.example.mtg_deckbuilder.views.api.BuilderOwnedLibraryView;
import com.example.mtg_deckbuilder.views.api.BuilderViewModel;
import com.example.mtg_deckbuilder.views.impl.BuilderCardQueryViewImpl;
import com.example.mtg_deckbuilder.views.impl.BuilderDeckLayoutViewImpl;
import com.example.mtg_deckbuilder.views.impl.BuilderMainViewImpl;
import com.example.mtg_deckbuilder.views.impl.BuilderOwnedLibraryViewImpl;
import com.example.mtg_deckbuilder.views.impl.BuilderViewModelImpl;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BuilderServiceImpl implements BuilderService {

    private final BuilderRepository builderRepository;
    private final DeckService deckService;
    private final CardService cardService;
    private final PersonalLibraryService personalLibraryService;

    public BuilderServiceImpl(BuilderRepository builderRepository,
                              DeckService deckService,
                              CardService cardService,
                              PersonalLibraryService personalLibraryService) {
        this.builderRepository = builderRepository;
        this.deckService = deckService;
        this.cardService = cardService;
        this.personalLibraryService = personalLibraryService;
    }

    @Override
    public BuilderMainView getMainView(String deckId, CustomUserDetails user) {
        return BuilderMainViewImpl.from(getBuilderView(deckId), user);
    }

    @Override
    public BuilderDeckLayoutView getDeckLayoutView(String deckId,
                                                   String viewStyle,
                                                   String groupBy,
                                                   String sortBy,
                                                   List<String> extrasParams) {
        return BuilderDeckLayoutViewImpl.of(
                getBuilderView(deckId),
                viewStyle,
                extrasParams,
                buildDeckSections(
                        deckId,
                        BuilderDeckLayoutViewImpl.normalizeGroupBy(groupBy),
                        BuilderDeckLayoutViewImpl.normalizeSortBy(sortBy)));
    }

    @Override
    public List<BuilderDeckSection> buildDeckSections(String deckId, String groupBy, String sortBy) {
        List<Card> cards = builderRepository.getAllCardsForUser(deckId);
        return BuilderDeckLayoutComposer.build(groupBy, sortBy, cards);
    }

    @Override
    public BuilderCardQueryView getCardQueryView(String query) {
        String normalizedQuery = BuilderCardQueryViewImpl.getString(query);
        return BuilderCardQueryViewImpl.of(normalizedQuery, personalLibraryService.getCardQuery(normalizedQuery));
    }

    @Override
    public BuilderOwnedLibraryView getOwnedLibraryView(String deckId, CustomUserDetails user) {
        return BuilderOwnedLibraryViewImpl.of(
                personalLibraryService.buildPersonalLibraryViewModel(user),
                deckId,
                getBuilderView(deckId).deckName());
    }

    @Override
    public Optional<BuilderCardHoverView> getDeckEntryHover(CustomUserDetails user, UUID deckId, UUID deckCardEntryId) {
        return builderRepository.findDeckEntryHover(user.getId(), deckId, deckCardEntryId);
    }


    @Override
    public List<OwnedCard> getCardsFromDeck(UUID deckId) {
        return builderRepository.getAllCardsFromDeck(deckId);
    }

    @Override
    public List<String> getRandomizedCards(UUID deckId) {
        var cards = builderRepository.getAllCardsFromDeck(deckId);
        Collections.shuffle(cards);
        // <img src> must be a plain https URL, not raw image_uris JSON (browser treats "{" as relative path).
        return cards.stream()
                .map(OwnedCard::getCard)
                .map(Card::artworkUrl)
                .filter(Objects::nonNull)
                .filter(img -> !img.isBlank())
                .limit(7)
                .toList();
    }

    @Override
    public void removeDeckEntry(CustomUserDetails user, UUID deckId, UUID deckCardEntryId) {
        deckService.removeDeckEntry(user, deckId, deckCardEntryId);
    }

    @Override
    public void addCardToDeck(CustomUserDetails user, String deckId, String cardName) {
        deckService.addCard(user, deckId, cardName);
    }

    @Override
    public BuilderViewModel getBuilderView(String deckId) {
        var cards = builderRepository.getAllCardsForUser(deckId);
        return BuilderViewModelImpl.fromCards(deckId, cards, cardService::findByName);
    }

    @Override
    public String optimizeDecksAgainstOpponent() {
        Map<String, Integer> oppDeck = new HashMap<>();

        oppDeck.put("1 CMC", 10);
        oppDeck.put("2 CMC", 15);
        oppDeck.put("3 CMC", 12);
        oppDeck.put("4 CMC", 8);
        oppDeck.put("5 CMC", 5);
        oppDeck.put("6 CMC", 2);
        oppDeck.put("Rock", 10);
        oppDeck.put("Draw", 0);
        oppDeck.put("Land", 36);
        oppDeck.put("Sol Ring", 1);
        DeckOptimizerV2 optimizer = new DeckOptimizerV2.Builder()
                .initial1Cmc(10)
                .initial2Cmc(15)
                .initial3Cmc(12)
                .initial4Cmc(8)
                .initial5Cmc(5)
                .initial6Cmc(2)
                .initialRock(10)
                .initialDraw(0)
                .initialLand(36)
                .commanderCost(4)
                .manaRockCost(2)
                .beatOtherMode(false)
                .oppCommander(5)
                .oppDecklist(oppDeck)
                .build();

        optimizer.run();
        return "";
    }

    @Override
    public String optimizeDeck() {
        return "";
    }
}
