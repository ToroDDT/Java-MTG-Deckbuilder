package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.exceptions.CardDoesNotExistException;
import com.example.mtg_deckbuilder.model.*;
import com.example.mtg_deckbuilder.repository.api.PersonalLibraryRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.views.PersonalLibraryStats;
import lombok.NonNull;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.jooq.generated.Tables.*;

@Repository
public class PersonalLibraryRepositoryImpl implements PersonalLibraryRepository {

  private final DSLContext dslContext;

  public PersonalLibraryRepositoryImpl(DSLContext dslContext) {
    this.dslContext = dslContext;
  }

@Override
  public void delete(@NonNull CustomUserDetails user, @NonNull String personalCardId) {
      int rowsChanged = dslContext.deleteFrom(PERSONAL_COLLECTION_LIBRARY)
              .where(PERSONAL_COLLECTION_LIBRARY.ID.eq(UUID.fromString(personalCardId)))
              .and(PERSONAL_COLLECTION_LIBRARY.USER_ID.eq(user.getId()))
              .execute();

      if (rowsChanged == 0) {
          throw new CardDoesNotExistException("Card could not be found in library: " + personalCardId);
      }
  }

  private List<String> getUpdatedCardTags(@NonNull UUID personalCardId, @NonNull CustomUserDetails user) {
      String[] tags = dslContext.select(PERSONAL_COLLECTION_LIBRARY.TAGS)
              .from(PERSONAL_COLLECTION_LIBRARY)
              .where(PERSONAL_COLLECTION_LIBRARY.ID.eq(personalCardId))
              .and(PERSONAL_COLLECTION_LIBRARY.USER_ID.eq(user.getId()))
              .fetchOne(PERSONAL_COLLECTION_LIBRARY.TAGS);

      return tags == null ? List.of() : Arrays.asList(tags);
  }

  @Override
  public List<String> updateTagsOnCard(String tag, UUID personalCardId, CustomUserDetails user) {
      // Postgres array_append using DSL.field, combined with RETURNING
      String[] updatedTags = dslContext.update(PERSONAL_COLLECTION_LIBRARY)
              .set(PERSONAL_COLLECTION_LIBRARY.TAGS, DSL.field(
                      "array_append(COALESCE({0}, ARRAY[]::text[]), {1})",
                      String[].class,
                      PERSONAL_COLLECTION_LIBRARY.TAGS,
                      tag
              ))
              .where(PERSONAL_COLLECTION_LIBRARY.ID.eq(personalCardId))
              .and(PERSONAL_COLLECTION_LIBRARY.USER_ID.eq(user.getId()))
              .returningResult(PERSONAL_COLLECTION_LIBRARY.TAGS)
              .fetchOneInto(String[].class);

      return updatedTags == null ? List.of() : Arrays.asList(updatedTags);
  }

  @Override
  public List<String> deleteTagFromCard(String tag, UUID personalCardId, CustomUserDetails user) {
      // Postgres array_remove using DSL.field, combined with RETURNING
      String[] updatedTags = dslContext.update(PERSONAL_COLLECTION_LIBRARY)
              .set(PERSONAL_COLLECTION_LIBRARY.TAGS, DSL.field(
                      "array_remove({0}, {1})",
                      String[].class,
                      PERSONAL_COLLECTION_LIBRARY.TAGS,
                      tag
              ))
              .where(PERSONAL_COLLECTION_LIBRARY.ID.eq(personalCardId))
              .and(PERSONAL_COLLECTION_LIBRARY.USER_ID.eq(user.getId()))
              .returningResult(PERSONAL_COLLECTION_LIBRARY.TAGS)
              .fetchOneInto(String[].class);

      return updatedTags == null ? List.of() : Arrays.asList(updatedTags);
  }

  @Override
  public List<OwnedCard> getAllPersonalLibraryCardsForUser(UUID userId) {
    return dslContext.select(
                    PERSONAL_COLLECTION_LIBRARY.ID.as("personal_library_id"),
                    PERSONAL_COLLECTION_LIBRARY.CARD_ID.as("card_id"),
                    PERSONAL_COLLECTION_LIBRARY.DATE_ADDED,
                    PERSONAL_COLLECTION_LIBRARY.TAGS,

                    CARDS.ID,
                    CARDS.NAME,
                    CARDS.TYPE_LINE,
                    CARDS.TOUGHNESS,
                    CARDS.POWER,
                    CARDS.ARTIST,
                    CARDS.CMC,
                    CARDS.SCRYFALL_URI,
                    CARDS.COLOR_IDENTITY,
                    CARDS.MULTIVERSE_IDS,
                    CARDS.IMAGE_URIS,
                    CARDS.PRICES
            )
            .from(CARDS)
            .join(PERSONAL_COLLECTION_LIBRARY)
            .on(PERSONAL_COLLECTION_LIBRARY.CARD_ID.eq(CARDS.ID))
            .where(PERSONAL_COLLECTION_LIBRARY.USER_ID.eq(userId))
            .orderBy(
                    PERSONAL_COLLECTION_LIBRARY.DATE_ADDED.desc(),
                    PERSONAL_COLLECTION_LIBRARY.ID.desc()
            )
            .fetch(OwnedCard::mapFromRecord);
  }

 @Override
public List<OwnedCard> getAllPersonalLibraryCardsForUserPaginated(UUID userId) {

    var pageSize = 12;

    return dslContext
        .select(
            PERSONAL_COLLECTION_LIBRARY.ID.as("personal_library_id"),
            PERSONAL_COLLECTION_LIBRARY.USER_ID,
            PERSONAL_COLLECTION_LIBRARY.DATE_ADDED,
            PERSONAL_COLLECTION_LIBRARY.UPDATED_AT,
            PERSONAL_COLLECTION_LIBRARY.TAGS,

            CARDS.ID,
            CARDS.NAME,
            CARDS.TYPE_LINE,
            CARDS.TOUGHNESS,
            CARDS.POWER,
            CARDS.ARTIST,
            CARDS.CMC,
            CARDS.SCRYFALL_URI,
            CARDS.COLOR_IDENTITY,
            CARDS.MULTIVERSE_IDS,
            CARDS.IMAGE_URIS,
            CARDS.PRICES
        )
        .from(CARDS)
        .join(PERSONAL_COLLECTION_LIBRARY)
        .on(PERSONAL_COLLECTION_LIBRARY.CARD_ID.eq(CARDS.ID))
        .where(PERSONAL_COLLECTION_LIBRARY.USER_ID.eq(userId))
        .orderBy(
            PERSONAL_COLLECTION_LIBRARY.DATE_ADDED.desc(),
            PERSONAL_COLLECTION_LIBRARY.ID.desc()
        )
        .limit(pageSize)
        .fetch(OwnedCard::mapFromRecord);
}

  @Override
public List<OwnedCard> getAllPersonalLibraryCardsForUser(UUID userId, LibraryFilters filters) {
    var pageSize = 12;
    int page = (filters.getPage() != null) ? Math.max(filters.getPage(), 0) : 0;

    // 1. Start with the base condition (User ID and CMC range)
Condition condition = PERSONAL_COLLECTION_LIBRARY.USER_ID.eq(userId)
        .and(CARDS.CMC.between(
            BigDecimal.valueOf(filters.getMinCMC()),
            BigDecimal.valueOf(filters.getMaxCMC())
        ));
    // 2. Dynamically append filters
    if (filters.getOracleTextSearch() != null && !filters.getOracleTextSearch().isEmpty()) {
        // Using Postgres Full Text Search via jOOQ plain SQL template
        condition = condition.and("to_tsvector('english', {0}) @@ plainto_tsquery('english', {1})",
                                  CARDS.ORACLE_TEXT, filters.getOracleTextSearch());
    }

    if (filters.getCardName() != null && !filters.getCardName().isEmpty()) {
        condition = condition.and(CARDS.NAME.containsIgnoreCase(filters.getCardName()));
    }

    if (filters.getCardType() != null && !"ALL".equalsIgnoreCase(filters.getCardType())) {
        String type = CardType.fromString(filters.getCardType()).getType();
        condition = condition.and(CARDS.TYPE_LINE.containsIgnoreCase(type));
    }

    if (filters.getSelectedColors() != null && !filters.getSelectedColors().isEmpty()) {
        String[] colors = filters.getSelectedColors().toArray(new String[0]);
        // Postgres Array contains (@>) logic
        condition = condition.and("{0} @> {1}::text[]", CARDS.COLOR_IDENTITY, colors)
                             .and("{1}::text[] @> {0}", CARDS.COLOR_IDENTITY, colors);
    }

    // 3. Handle Tag Tokens (Postgres unnest logic)
    for (String token : filters.tagSearchTokens()) {
        condition = condition.and(DSL.exists(
            DSL.selectOne()
               .from(DSL.unnest(DSL.coalesce(PERSONAL_COLLECTION_LIBRARY.TAGS, DSL.array())).as("tag_row", "tag_value"))
               .where(DSL.field("tag_value").containsIgnoreCase(token))
        ));
    }

    // 4. Handle Sorting
    SortField<?> sortOrder = switch (filters.getSortBy()) {
        case PRICE_ASC -> DSL.field("{0}->>'usd'", Double.class, CARDS.PRICES).asc().nullsLast();
        case PRICE_DESC -> DSL.field("{0}->>'usd'", Double.class, CARDS.PRICES).desc().nullsLast();
        case CMC_ASC    -> CARDS.CMC.asc().nullsLast();
        case CMC_DESC   -> CARDS.CMC.desc().nullsLast();
        case NAME_DESC  -> CARDS.NAME.desc();
        default         -> PERSONAL_COLLECTION_LIBRARY.DATE_ADDED.desc();
    };

    // 5. Execute and Map
    return dslContext.select(
                PERSONAL_COLLECTION_LIBRARY.ID.as("personal_library_id"),
                PERSONAL_COLLECTION_LIBRARY.USER_ID,
                PERSONAL_COLLECTION_LIBRARY.DATE_ADDED,
                PERSONAL_COLLECTION_LIBRARY.TAGS,
                CARDS.ID,
                CARDS.NAME,
                CARDS.TYPE_LINE,
                CARDS.TOUGHNESS,
                CARDS.POWER,
                CARDS.ARTIST,
                CARDS.CMC,
                CARDS.SCRYFALL_URI,
                CARDS.COLOR_IDENTITY
            )
            .from(CARDS)
            .join(PERSONAL_COLLECTION_LIBRARY).on(PERSONAL_COLLECTION_LIBRARY.CARD_ID.eq(CARDS.ID))
            .where(condition)
            .orderBy(sortOrder, PERSONAL_COLLECTION_LIBRARY.ID.desc())
            .limit(pageSize)
            .offset(page * pageSize)
            .fetch(OwnedCard::mapFromRecord);
}

@Override
public void addCardToPersonalLibrary(OwnedCard ownedCard) {
    dslContext.insertInto(PERSONAL_COLLECTION_LIBRARY)
            .set(PERSONAL_COLLECTION_LIBRARY.USER_ID, ownedCard.getUserId())
            .set(PERSONAL_COLLECTION_LIBRARY.CARD_ID, ownedCard.getCardId())
            .set(PERSONAL_COLLECTION_LIBRARY.IMAGE, ownedCard.getImage())
            .execute();
}

 @Override
public Map<UUID, List<String>> getDeckLocationsOfCards(CustomUserDetails user, List<UUID> cardIds) {
    if (cardIds == null || cardIds.isEmpty()) {
        return Map.of();
    }

    // fetchGroups is the jOOQ equivalent of the Java Collectors.groupingBy logic
    return dslContext.select(
                DECK_CARD_ENTRIES.PERSONAL_LIBRARY_CARD_ID,
                DECKS.NAME
            )
            .from(DECK_CARD_ENTRIES)
            .join(DECKS).on(DECKS.ID.eq(DECK_CARD_ENTRIES.DECK_ID))
            .where(DECKS.USER_ID.eq(user.getId()))
            .and(DECK_CARD_ENTRIES.PERSONAL_LIBRARY_CARD_ID.in(cardIds))
            .orderBy(DECK_CARD_ENTRIES.PERSONAL_LIBRARY_CARD_ID, DECKS.NAME)
            .fetchGroups(
                record -> record.get(DECK_CARD_ENTRIES.PERSONAL_LIBRARY_CARD_ID),
                record -> record.get(DECKS.NAME)
            );
}

 @Override
public PersonalLibraryStats getStatsOfPersonalLibrary(CustomUserDetails user) {
    UUID userId = user.getId();

    // 1. Fetch the cards using our new mapper
    List<OwnedCard> cards = dslContext.select(
                PERSONAL_COLLECTION_LIBRARY.ID.as("personal_library_id"),
                PERSONAL_COLLECTION_LIBRARY.USER_ID,
                PERSONAL_COLLECTION_LIBRARY.DATE_ADDED,
                PERSONAL_COLLECTION_LIBRARY.TAGS,
                CARDS.ID,
                CARDS.NAME,
                CARDS.TYPE_LINE,
                CARDS.ARTIST,
                CARDS.CMC,
                CARDS.COLOR_IDENTITY,
                CARDS.IMAGE_URIS,
                CARDS.PRICES
            )
            .from(CARDS)
            .join(PERSONAL_COLLECTION_LIBRARY)
                .on(PERSONAL_COLLECTION_LIBRARY.CARD_ID.eq(CARDS.ID))
            .where(PERSONAL_COLLECTION_LIBRARY.USER_ID.eq(userId))
            .orderBy(PERSONAL_COLLECTION_LIBRARY.DATE_ADDED.desc(), PERSONAL_COLLECTION_LIBRARY.ID.desc())
            .fetch(OwnedCard::mapFromRecord);

    // 2. Calculate the total value (Handling the JSONB prices column)
    double totalValue = cards.stream()
            .filter(c -> c.getCard() != null && c.getCard().getPrices() != null)
            .mapToDouble(c -> {
                Double price = c.getCard().getPrices().getUsd();
                return price != null ? price : 0.0;
            })
            .sum();

    // 3. Aggregate Color Counts
    var colorCounts = cards.stream()
            .collect(Collectors.groupingBy(
                    ColorIdentity::fromString,
                Collectors.counting()
            ));

    // 4. Final Stats
    int totalCards = cards.size();
    double avgPrice = totalCards == 0 ? 0.0 : totalValue / totalCards;

    return new PersonalLibraryStats(totalValue, colorCounts, totalCards, avgPrice);
}
}
