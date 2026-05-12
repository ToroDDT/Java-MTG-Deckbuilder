package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.exceptions.CardDoesNotExistException;
import com.example.mtg_deckbuilder.model.*;
import com.example.mtg_deckbuilder.repository.api.PersonalLibraryRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.views.PersonalLibraryStats;
import lombok.NonNull;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.mtg_deckbuilder.jooq.Tables.CARDS;
import static com.example.mtg_deckbuilder.jooq.Tables.PERSONAL_COLLECTION_LIBRARY;

@Repository
public class PersonalLibraryRepositoryImpl implements PersonalLibraryRepository {

  private final DSLContext dsl;

  public PersonalLibraryRepositoryImpl(DSLContext dsl) {
    this.dsl = dsl;
  }

private final RecordMapper<Record, OwnedCard> ownedCardMapper = r -> {

    Card card = new Card();

    card.setId(r.get(CARDS.ID));
    card.setName(r.get(CARDS.NAME));
    card.setTypeLine(r.get(CARDS.TYPE_LINE));
    card.setToughness(r.get(CARDS.TOUGHNESS));
    card.setPower(r.get(CARDS.POWER));
    card.setArtist(r.get(CARDS.ARTIST));

    card.setCmc((Integer) r.get((Name) CARDS.CMC));

    card.setScryfallUri(r.get(CARDS.SCRYFALL_URI));

    card.setColorIdentity(
            Optional.ofNullable(r.get(CARDS.COLOR_IDENTITY))
                    .map(Arrays::asList)
                    .orElse(List.of())
    );

    card.setMultiverseIds(r.get(CARDS.MULTIVERSE_IDS));

    card.setImageUris(r.get(CARDS.IMAGE_URIS));

    card.setPrices(r.get(CARDS.PRICES));

    return new OwnedCard(
            card,
            r.get(PERSONAL_COLLECTION_LIBRARY.ID),
            r.get(PERSONAL_COLLECTION_LIBRARY.CARD_ID),
            Optional.ofNullable(r.get(PERSONAL_COLLECTION_LIBRARY.DATE_ADDED))
                    .map(d -> d.toLocalDate())
                    .orElse(null),
            Optional.ofNullable(r.get(PERSONAL_COLLECTION_LIBRARY.TAGS))
                    .map(Arrays::asList)
                    .orElse(List.of())
    );
};
  @Override
  public void delete(@NonNull CustomUserDetails user, @NonNull String personalCardId) {
    int deleted = dsl.deleteFrom(PERSONAL_COLLECTION_LIBRARY)
            .where(PERSONAL_COLLECTION_LIBRARY.ID.eq(UUID.fromString(personalCardId)))
            .and(PERSONAL_COLLECTION_LIBRARY.USER_ID.eq(user.getId()))
            .execute();

    if (deleted == 0) {
      throw new CardDoesNotExistException(personalCardId);
    }
  }

  @Override
  public List<String> updateTagsOnCard(String tag, UUID personalCardId, CustomUserDetails user) {
    dsl.update(PERSONAL_COLLECTION_LIBRARY)
            .set(PERSONAL_COLLECTION_LIBRARY.TAGS,
                    DSL.field("array_append(COALESCE(tags, ARRAY[]::text[]), {0})", String[].class, tag))
            .where(PERSONAL_COLLECTION_LIBRARY.ID.eq(personalCardId))
            .and(PERSONAL_COLLECTION_LIBRARY.USER_ID.eq(user.getId()))
            .execute();

    return getUpdatedCardTags(personalCardId, user);
  }

  @Override
  public List<String> deleteTagFromCard(String tag, UUID personalCardId, CustomUserDetails user) {
    dsl.update(PERSONAL_COLLECTION_LIBRARY)
            .set(PERSONAL_COLLECTION_LIBRARY.TAGS,
                    DSL.field("array_remove(tags, {0})", String[].class, tag))
            .where(PERSONAL_COLLECTION_LIBRARY.ID.eq(personalCardId))
            .and(PERSONAL_COLLECTION_LIBRARY.USER_ID.eq(user.getId()))
            .execute();

    return getUpdatedCardTags(personalCardId, user);
  }

  private List<String> getUpdatedCardTags(UUID personalCardId, CustomUserDetails user) {
    return dsl.select(PERSONAL_COLLECTION_LIBRARY.TAGS)
            .from(PERSONAL_COLLECTION_LIBRARY)
            .where(PERSONAL_COLLECTION_LIBRARY.ID.eq(personalCardId))
            .and(PERSONAL_COLLECTION_LIBRARY.USER_ID.eq(user.getId()))
            .fetchOptional()
            .map(Record1::value1)
            .map(Arrays::asList)
            .orElse(List.of());
  }

  @Override
  public List<OwnedCard> getAllPersonalLibraryCardsForUser(UUID userId) {
    return dsl.select()
            .from(CARDS)
            .join(PERSONAL_COLLECTION_LIBRARY)
            .on(PERSONAL_COLLECTION_LIBRARY.CARD_ID.eq(CARDS.ID))
            .where(PERSONAL_COLLECTION_LIBRARY.USER_ID.eq(userId))
            .orderBy(
                    PERSONAL_COLLECTION_LIBRARY.DATE_ADDED.desc(),
                    PERSONAL_COLLECTION_LIBRARY.ID.desc()
            )
            .fetch(ownedCardMapper);
  }

  @Override
  public List<OwnedCard> getAllPersonalLibraryCardsForUserPaginated(UUID userId) {
    int pageSize = 12;

    return dsl.select()
            .from(CARDS)
            .join(PERSONAL_COLLECTION_LIBRARY)
            .on(PERSONAL_COLLECTION_LIBRARY.CARD_ID.eq(CARDS.ID))
            .where(PERSONAL_COLLECTION_LIBRARY.USER_ID.eq(userId))
            .orderBy(
                    PERSONAL_COLLECTION_LIBRARY.DATE_ADDED.desc(),
                    PERSONAL_COLLECTION_LIBRARY.ID.desc()
            )
            .limit(pageSize)
            .fetch(ownedCardMapper);
  }

  @Override
  public List<OwnedCard> getAllPersonalLibraryCardsForUser(UUID userId, LibraryFilters filters) {
var p = PERSONAL_COLLECTION_LIBRARY;
var c = CARDS;

int pageSize = 12;
int page = filters.getPage() == null ? 0 : Math.max(filters.getPage(), 0);

var query = dsl.select()
        .from(c)
        .join(p).on(p.CARD_ID.eq(c.ID))
        .where(p.USER_ID.eq(userId));

if (filters.getCardName() != null && !filters.getCardName().isEmpty()) {
    query = query.and(c.NAME.likeIgnoreCase(filters.getCardName() + "%"));
}

if (filters.getCardType() != null && !filters.getCardType().isEmpty()
        && !"ALL".equalsIgnoreCase(filters.getCardType())) {

    String type = CardType.fromString(filters.getCardType()).getType();
    query = query.and(c.TYPE_LINE.likeIgnoreCase("%" + type + "%"));
}

if (filters.getSelectedColors() != null && !filters.getSelectedColors().isEmpty()) {
    String[] colors = filters.getSelectedColors().toArray(new String[0]);
    query = query.and(c.COLOR_IDENTITY.contains(colors));
}

if (filters.getOracleTextSearch() != null && !filters.getOracleTextSearch().isEmpty()) {
    query = query.and(DSL.condition(
            "to_tsvector('english', {0}) @@ plainto_tsquery('english', {1})",
            c.ORACLE_TEXT,
            filters.getOracleTextSearch()
    ));
}

for (String token : filters.tagSearchTokens()) {
    query = query.andExists(
            DSL.selectOne()
                    .from(DSL.unnest(p.TAGS).as("t", "tag_value"))
                    .where(DSL.field("tag_value", String.class)
                            .likeIgnoreCase("%" + token + "%"))
    );
}

query = query.and(
        c.CMC.between(
                DSL.val(BigDecimal.valueOf(filters.getMinCMC())),
                DSL.val(BigDecimal.valueOf(filters.getMaxCMC()))
        )
);

var priceUsd = DSL.field(
        "NULLIF({0}->>'usd','')::numeric",
        BigDecimal.class,
        c.PRICES
);

var sortedQuery =
        switch (filters.getSortBy()) {

            case PRICE_ASC -> query.orderBy(priceUsd.asc().nullsLast());

            case PRICE_DESC -> query.orderBy(priceUsd.desc().nullsLast());

            case CMC_ASC -> query.orderBy(c.CMC.asc());

            case CMC_DESC -> query.orderBy(c.CMC.desc());

            case NAME_DESC -> query.orderBy(c.NAME.desc());

            default -> query.orderBy(
                    p.DATE_ADDED.desc(),
                    p.ID.desc()
            );
        };

return sortedQuery
        .limit(pageSize)
        .offset(page * pageSize)
        .fetch(ownedCardMapper);
  }

  @Override
  public void addCardToPersonalLibrary(OwnedCard ownedCard) {
    dsl.insertInto(PERSONAL_COLLECTION_LIBRARY)
            .set(PERSONAL_COLLECTION_LIBRARY.USER_ID, ownedCard.getUserId())
            .set(PERSONAL_COLLECTION_LIBRARY.CARD_ID, ownedCard.getCardId())
            .set(PERSONAL_COLLECTION_LIBRARY.IMAGE, ownedCard.getImage())
            .execute();
  }

  @Override
  public Map<UUID, List<String>> getDeckLocationsOfCards(CustomUserDetails user, List<UUID> cardIds) {
    if (cardIds == null || cardIds.isEmpty()) return Map.of();

    return dsl.select(
                    DSL.field("dce.personal_library_card_id", UUID.class),
                    DSL.field("deck.name", String.class)
            )
            .from(DSL.table("deck_card_entries").as("dce"))
            .join(DSL.table("decks").as("deck"))
            .on(DSL.field("deck.id").eq(DSL.field("dce.deck_id")))
            .where(DSL.field("deck.user_id").eq(user.getId()))
            .and(DSL.field("dce.personal_library_card_id").in(cardIds))
            .fetch()
            .stream()
            .collect(Collectors.groupingBy(
                    Record2::value1,
                    Collectors.mapping(Record2::value2, Collectors.toList())
            ));
  }

  @Override
  public PersonalLibraryStats getStatsOfPersonalLibrary(CustomUserDetails user) {

    var cards = getAllPersonalLibraryCardsForUser(user.getId());

    double totalValue = cards.stream()
            .filter(c -> c.getCard() != null && c.getCard().getPrices() != null)
            .filter(c -> c.getCard().getPrices().getUsd() != null)
            .mapToDouble(c -> c.getCard().getPrices().getUsd())
            .sum();

    var colorCounts = cards.stream()
            .collect(Collectors.groupingBy(
                    ColorIdentity::fromString,
                    Collectors.counting()
            ));

    int totalCards = cards.size();
    double avgPrice = totalCards == 0 ? 0.0 : totalValue / totalCards;

    return new PersonalLibraryStats(totalValue, colorCounts, totalCards, avgPrice);
  }
}