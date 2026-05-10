package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.exceptions.CardDoesNotExistException;
import com.example.mtg_deckbuilder.model.*;
import com.example.mtg_deckbuilder.repository.impl.PersonalLibraryRepositoryImpl;
import com.example.mtg_deckbuilder.repository.api.PersonalLibraryRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.service.api.CardService;
import com.example.mtg_deckbuilder.service.api.DeckService;
import com.example.mtg_deckbuilder.service.api.PersonalLibraryService;
import com.example.mtg_deckbuilder.views.LibraryViewModelImpl;
import com.example.mtg_deckbuilder.views.PersonalLibraryStats;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class PersonalLibraryServiceImpl implements PersonalLibraryService {
  private final PersonalLibraryRepository personalLibraryRepository;
  private final CardService cardServiceImpl;
  private final DeckService deckServiceImpl;

  public PersonalLibraryServiceImpl(PersonalLibraryRepositoryImpl personalLibraryRepository,
                                    CardService cardServiceImpl, DeckServiceImpl deckServiceImpl) {
    this.personalLibraryRepository = personalLibraryRepository;
    this.cardServiceImpl = cardServiceImpl;
    this.deckServiceImpl = deckServiceImpl;
  }

  @Override
  public void delete(CustomUserDetails user, String cardId) {
    personalLibraryRepository.delete(user, cardId);
  }

  @Override
  public List<String> updateCardTags(String tag, String personalCardId, CustomUserDetails user) {
    return personalLibraryRepository.updateTagsOnCard(tag, UUID.fromString(personalCardId), user);
  }

  @Override
  public List<String> removeCardTag(String tag, String personalCardId, CustomUserDetails user) {
    return personalLibraryRepository.deleteTagFromCard(tag, UUID.fromString(personalCardId), user);
  }

  @Override
  public List<Card> getCardQuery(String query) {
    return query.isEmpty()
            ? List.of()
            : cardServiceImpl.findByNameContaining(query).stream().limit(8).toList();
  }

  @Override
  public void addCard(OwnedCard ownedCard, UUID user) throws CardDoesNotExistException {
    var card = cardServiceImpl.findByName(ownedCard.getName());
    if (card.isPresent()) {
      ownedCard.setId(card.get().getId());
      ownedCard.setCardId(card.get().getId());
      ownedCard.setTags(List.of());
      ownedCard.setImage(card.get().getImage());
      ownedCard.setUserId(user);
      personalLibraryRepository.addCardToPersonalLibrary(ownedCard);
    } else {
      throw new CardDoesNotExistException(ownedCard.getName());
    }
  }

  @Override
  public List<OwnedCard> getCards(UUID userId) {
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
  public List<OwnedCard> getCardsPaginated(UUID userId) {
    return personalLibraryRepository
            .getAllPersonalLibraryCardsForUserPaginated(userId)
            .stream()
            .peek(ownedCard -> {
              if (ownedCard.getTags() == null || ownedCard.getTags().isEmpty()) {
                ownedCard.setTags(List.of());
              }
            })
            .toList();
  }

  @Override
  public List<OwnedCard> getCards(UUID userid, LibraryFilters personalLibraryFilters) {

    return personalLibraryRepository
            .getAllPersonalLibraryCardsForUser(userid, personalLibraryFilters)
            .stream()
            .peek(ownedCard -> {
              if (ownedCard.getTags() == null || ownedCard.getTags().isEmpty()) {
                ownedCard.setTags(List.of());
              }
            })
            .toList();
  }

  @Override
  public LibraryViewModelImpl buildPersonalLibraryViewModel(CustomUserDetails user) {

    var cardsFuture = CompletableFuture.supplyAsync(() -> this.getCardsPaginated(user.getId()));

    var deckNamesFuture = CompletableFuture.supplyAsync(() -> getDeckNames(user));

    var cards = cardsFuture.join();
    hydrateDeckLocations(user, cards);
    var lastCard = cards.isEmpty() ? null : cards.getLast().getDateAdded();

    var deckNames = deckNamesFuture.join();
    // Calculate total value
    var total = getTotalValue(cards);
    var colorCounts = getColorCount(user);

    // Use the Builder to assemble the object
    return LibraryViewModelImpl.builder()
            .cards(cards)
            .dateAdded(lastCard)
            .deckNames(deckNames)
            .totalCards(cards.size())
            .totalValue(total)
            .avgPrice(cards.isEmpty() ? 0.0 : total / cards.size())
            .colorIdentityAmounts(colorCounts)
            .build();
  }

  @Override
  public LibraryViewModelImpl buildPersonalLibraryViewModel(CustomUserDetails userId,
                                                            LibraryFilters personalLibraryFilters) {
    var cardsFuture = CompletableFuture.supplyAsync(() -> this.getCards(userId.getId(), personalLibraryFilters));

    var deckNamesFuture = CompletableFuture.supplyAsync(() -> getDeckNames(userId));

    var cards = cardsFuture.join();
    hydrateDeckLocations(userId, cards);
    var deckNames = deckNamesFuture.join();

    var total = getTotalValue(cards);
    var colorCounts = getColorCount(userId, personalLibraryFilters);
    boolean hasSearchFilter = personalLibraryFilters.hasSearchFilter();

    var lastCard = cards.isEmpty() ? null
            : hasSearchFilter ? null // frontend uses page+1 itself
            : cards.getLast().getDateAdded();

    // Use the Builder to assemble the object
    return LibraryViewModelImpl.builder()
            .cards(cards)
            .dateAdded(lastCard)
            .deckNames(deckNames)
            .totalCards(cards.size())
            .totalValue(total)
            .avgPrice(cards.isEmpty() ? 0.0 : total / cards.size())
            .colorIdentityAmounts(colorCounts)
            .build();
  }

  public Map<UUID, List<String>> getDeckLocationsOfCards(CustomUserDetails user) {
    var cards = personalLibraryRepository.getAllPersonalLibraryCardsForUser(user.getId())
            .stream()
            .map(OwnedCard::getId)
            .toList();
    return personalLibraryRepository.getDeckLocationsOfCards(user, cards);
  }

  private void hydrateDeckLocations(CustomUserDetails user, List<OwnedCard> cards) {
    if (cards == null || cards.isEmpty()) {
      return;
    }

    Map<UUID, List<String>> deckLocations = personalLibraryRepository.getDeckLocationsOfCards(
            user,
            cards.stream().map(OwnedCard::getId).toList());

    cards.forEach(card -> card.setDeckLocations(
            deckLocations.getOrDefault(card.getId(), List.of())));
  }

  @Override
  public PersonalLibraryStats getStatsOfPersonalLibrary(CustomUserDetails user) {
    return personalLibraryRepository.getStatsOfPersonalLibrary(user);
  }

  private Double getTotalValue(List<OwnedCard> cards) {
    // Calculate total value
    return cards.stream()
            .filter(c -> c.getCard() != null && c.getCard().getPrices().getUsd() != null)
            .mapToDouble(c -> c.getCard().getPrices().getUsd())
            .sum();
  }

  @Override
  public Map<ColorIdentity, Long> getAmountOfEachColorIdentity(UUID userId) {
    return personalLibraryRepository.getAllPersonalLibraryCardsForUser(userId)
            .stream()
            .collect(Collectors.groupingBy(ColorIdentity::fromString, Collectors.counting()));
  }

  public Map<ColorIdentity, Long> getAmountOfEachColorIdentity(UUID userId, LibraryFilters personalLibraryFilters) {
    return personalLibraryRepository.getAllPersonalLibraryCardsForUser(userId, personalLibraryFilters)
            .stream()
            .collect(Collectors.groupingBy(ColorIdentity::fromString, Collectors.counting()));
  }

  private Map<String, Long> getColorCount(CustomUserDetails userId) {
    // Format color counts
    Map<String, Long> colorCounts = new HashMap<>();
    getAmountOfEachColorIdentity(userId.getId())
            .forEach((key, value) -> colorCounts.put(key.name(), value));
    return colorCounts;
  }

  private Map<String, Long> getColorCount(CustomUserDetails userId, LibraryFilters personalLibraryFilters) {
    // Format color counts
    Map<String, Long> colorCounts = new HashMap<>();
    getAmountOfEachColorIdentity(userId.getId(), personalLibraryFilters)
            .forEach((key, value) -> colorCounts.put(key.name(), value));
    return colorCounts;
  }

  private List<String> getDeckNames(CustomUserDetails userId) {
    return deckServiceImpl.getDeckNames(userId);
  }
}
