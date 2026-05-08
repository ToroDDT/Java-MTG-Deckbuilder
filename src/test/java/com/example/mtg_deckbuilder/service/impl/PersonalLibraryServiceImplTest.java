package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.exceptions.CardDoesNotExistException;
import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.model.Prices;
import com.example.mtg_deckbuilder.model.SortOptions;
import com.example.mtg_deckbuilder.repository.impl.PersonalLibraryRepositoryImpl;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.CardService;
import com.example.mtg_deckbuilder.views.LibraryViewModelImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonalLibraryServiceImplTest {

    @Mock
    private PersonalLibraryRepositoryImpl personalLibraryRepository;

    @Mock
    private CardService cardServiceImpl;

    @Mock
    private DeckServiceImpl deckServiceImpl;

    @InjectMocks
    private PersonalLibraryServiceImpl personalLibraryService;

    @Test
    void addCardPopulatesOwnedCardAndPersistsIt() {
        UUID userId = UUID.randomUUID();
        UUID cardId = UUID.randomUUID();
        OwnedCard ownedCard = new OwnedCard();
        ownedCard.setName("Sol Ring");

        Card foundCard = new Card();
        foundCard.setId(cardId);
        foundCard.setImage("image-url");

        when(cardServiceImpl.findByName("Sol Ring")).thenReturn(Optional.of(foundCard));

        personalLibraryService.addCard(ownedCard, userId);

        ArgumentCaptor<OwnedCard> captor = ArgumentCaptor.forClass(OwnedCard.class);
        verify(personalLibraryRepository).addCardToPersonalLibrary(captor.capture());

        OwnedCard savedCard = captor.getValue();
        assertEquals(cardId, savedCard.getId());
        assertEquals(cardId, savedCard.getCardId());
        assertEquals("image-url", savedCard.getImage());
        assertEquals(userId, savedCard.getUserId());
        assertEquals(LocalDate.now(), savedCard.getDateAdded());
        assertEquals(List.of(), savedCard.getTags());
    }

    @Test
    void addCardThrowsWhenCardLookupFails() {
        OwnedCard ownedCard = new OwnedCard();
        ownedCard.setName("Missing Card");

        when(cardServiceImpl.findByName("Missing Card")).thenReturn(Optional.empty());

        assertThrows(CardDoesNotExistException.class,
                () -> personalLibraryService.addCard(ownedCard, UUID.randomUUID()));
    }

    @Test
    void updateCardTagsDelegatesUsingPersonalCardId() {
        UUID personalCardId = UUID.randomUUID();
        CustomUserDetails user = testUser();
        when(personalLibraryRepository.updateTagsOnCard("Ramp", personalCardId, user))
                .thenReturn(List.of("Ramp"));

        List<String> tags = personalLibraryService.updateCardTags("Ramp", personalCardId.toString(), user);

        assertEquals(List.of("Ramp"), tags);
        verify(personalLibraryRepository).updateTagsOnCard("Ramp", personalCardId, user);
    }

    @Test
    void getCardsSortsByPriceDescending() {
        LibraryFilters filters = new LibraryFilters();
        filters.setSortBy(SortOptions.PRICE_DESC);

        OwnedCard cheaper = ownedCard("Arcane Signet", 1.0, UUID.randomUUID());
        OwnedCard pricier = ownedCard("Mana Crypt", 10.0, UUID.randomUUID());

        when(personalLibraryRepository.getAllPersonalLibraryCardsForUser(any(UUID.class), eq(filters)))
                .thenReturn(List.of(cheaper, pricier));

        List<OwnedCard> result = personalLibraryService.getCards(UUID.randomUUID(), filters);

        assertIterableEquals(List.of(pricier, cheaper), result);
    }

    @Test
    void buildPersonalLibraryViewModelHydratesDeckLocationsAndTotals() {
        CustomUserDetails user = testUser();
        UUID firstOwnedId = UUID.randomUUID();
        UUID secondOwnedId = UUID.randomUUID();

        OwnedCard first = ownedCard("Sol Ring", 2.5, firstOwnedId);
        first.setDateAdded(LocalDate.of(2026, 4, 1));
        OwnedCard second = ownedCard("Arcane Signet", 1.5, secondOwnedId);
        second.setDateAdded(LocalDate.of(2026, 4, 2));

        when(personalLibraryRepository.getAllPersonalLibraryCardsForUserPaginated(user.getId()))
                .thenReturn(List.of(first, second));
        when(personalLibraryRepository.getDeckLocationsOfCards(user, List.of(firstOwnedId, secondOwnedId)))
                .thenReturn(Map.of(
                        firstOwnedId, List.of("Artifacts"),
                        secondOwnedId, List.of("Budget")
                ));
        when(deckServiceImpl.getDeckNames(user)).thenReturn(List.of("Artifacts", "Budget"));

        LibraryViewModelImpl viewModel = personalLibraryService.buildPersonalLibraryViewModel(user);

        assertEquals(2, viewModel.getCards().size());
        assertEquals(4.0, viewModel.getTotalValue());
        assertEquals(2.0, viewModel.getAvgPrice());
        assertEquals(List.of("Artifacts"), viewModel.getCards().getFirst().getDeckLocations());
        assertEquals(List.of("Budget"), viewModel.getCards().get(1).getDeckLocations());
        assertNotNull(viewModel.getDeckNames());
    }

    private static CustomUserDetails testUser() {
        return new CustomUserDetails(
                "tester",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                UUID.randomUUID()
        );
    }

    private static OwnedCard ownedCard(String name, double price, UUID ownedId) {
        Card card = new Card();
        card.setId(UUID.randomUUID());
        card.setName(name);
        card.setCmc(2);
        card.setColorIdentity(List.of());
        card.setPrices(Prices.builder().usd(price).build());

        OwnedCard ownedCard = new OwnedCard();
        ownedCard.setId(ownedId);
        ownedCard.setCardId(card.getId());
        ownedCard.setCard(card);
        ownedCard.setTags(List.of());
        ownedCard.setDateAdded(LocalDate.of(2026, 4, 1));
        return ownedCard;
    }
}
