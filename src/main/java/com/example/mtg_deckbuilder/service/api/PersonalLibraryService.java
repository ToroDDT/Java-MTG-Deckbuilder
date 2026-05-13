package com.example.mtg_deckbuilder.service.api;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.model.ColorIdentity;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.views.LibraryViewModelImpl;
import com.example.mtg_deckbuilder.views.PersonalLibraryStats;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PersonalLibraryService {
/**
   * Adds a card to the personal library of the specified user.
   *
   * @param ownedCard the card to be added, including its associated details;
   *                  must not be null. OwnedCard represents a card the user
   *                  currently owns, not a card in a deck being built.
   * @param userId the unique identifier of the user to whose library the card
   *               will be added; must not be null.
   * @throws NullPointerException if {@code ownedCard} or {@code userId} is null.
   */
  void addCard(OwnedCard ownedCard, UUID userId);

/**
   * Retrieves all cards currently stored in the user's personal library.
   *
   * @param userId the unique identifier of the user whose library is being retrieved;
   *               must not be null.
   * @return a list of {@code OwnedCard} objects representing the user's collection;
   *         will not be null but may be empty if the user owns no cards.
   * @throws NullPointerException if {@code userId} is null.
   */

  List<OwnedCard> getCards(UUID userId);

  /**
   * Retrieves a paginated list of cards from the user's personal library.
   *
   * @param userId the unique identifier of the user whose paginated library is being retrieved;
   *               must not be null.
   * @return a paginated list of {@code OwnedCard} objects representing a subset of the user's
   *         collection; will not be null but may be empty if the user owns no cards.
   *         Pagination implementation details depend on the specific service logic.
   */

  List<OwnedCard> getCardsPaginated(UUID userId);

  /**
   * Retrieves a filtered list of cards from the personal library of the specified user.
   *
   * @param userId the unique identifier of the user whose library is being queried;
   *               must not be null.
   * @param personalLibraryFilters the filters to apply when retrieving cards from the user's library;
   *                                may include properties such as card name, colors, type, and other criteria; must not be null.
   * @return a list of {@code OwnedCard} objects that match the specified filters; the list will not be null
   *         but may be empty if no cards match the filters or if the user owns no cards.
   */
  List<OwnedCard> getCards(UUID userId, LibraryFilters personalLibraryFilters);


  /**
   * Retrieves the count of cards grouped by each {@code ColorIdentity}
   * from the personal library of a specific user.
   *
   * @param userId the unique identifier of the user whose card color identity counts are being queried;
   *               must not be null.
   * @return a {@code Map} where the keys represent {@code ColorIdentity}
   *         and the values are the counts of cards corresponding to each color identity;
   *         the map will not be null but may be empty if the user owns no cards.
   */
  Map<ColorIdentity, Long> getAmountOfEachColorIdentity(UUID userId);

  LibraryViewModelImpl buildPersonalLibraryViewModel(CustomUserDetails user);

  LibraryViewModelImpl buildPersonalLibraryViewModel(CustomUserDetails user, LibraryFilters personalLibraryFilters);

  PersonalLibraryStats getStatsOfPersonalLibrary(CustomUserDetails user);

  List<Card> getCardQuery(String query);

  List<String> updateCardTags(String tag, String personalCardId, CustomUserDetails user);

  List<String> removeCardTag(String tag, String personalCardId, CustomUserDetails user);

  /**
   * Removes a specific card from the personal library of the specified user.
   *
   * @param user the user whose personal library contains the card to be removed; must not be null.
   * @param cardId the unique identifier of the card to be removed from the user's library; must not be null.
   */
 void delete(CustomUserDetails user, String cardId);
}
