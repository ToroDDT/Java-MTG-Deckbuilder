package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.mapper.OwnedCardRowMapper;
import com.example.mtg_deckbuilder.model.*;
import com.example.mtg_deckbuilder.repository.api.PersonalLibraryRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.views.PersonalLibraryStats;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class PersonalLibraryRepositoryImpl implements PersonalLibraryRepository {

  private final JdbcClient jdbcClient;
  private final JdbcTemplate jdbcTemplate;
  private final OwnedCardRowMapper ownedCardRowMapper;

  public PersonalLibraryRepositoryImpl(OwnedCardRowMapper ownedCardRowMapper, JdbcClient jdbcClient,
                                       JdbcTemplate jdbcTemplate) {
    this.jdbcClient = jdbcClient;
    this.jdbcTemplate = jdbcTemplate;
    this.ownedCardRowMapper = ownedCardRowMapper;
  }


  public void delete(CustomUserDetails user, String personalCardId) {
    String sql = """
        DELETE FROM personal_collection_library
        WHERE id = :personalCardId AND user_id = :userId
        """;

    jdbcClient.sql(sql)
            .param("personalCardId", UUID.fromString(personalCardId))
            .param("userId", user.getId())
            .update();
  }
  private List<String> getUpdatedCardTags(UUID personalCardId, CustomUserDetails user) {
    String sql = """
        SELECT tags
        FROM personal_collection_library
        WHERE id = :personalCardId AND user_id = :userId
        """;

    return jdbcClient.sql(sql)
            .param("personalCardId", personalCardId)
            .param("userId", user.getId())
            .query((rs, rowNum) -> {
              var sqlArray = rs.getArray("tags");
              if (sqlArray == null) {
                return new String[0];
              }
              return (String[]) sqlArray.getArray();
            })
            .list()
            .stream()
            .flatMap(Arrays::stream)
            .toList();
  }

  @Override
  public List<String> updateTagsOnCard(String tag, UUID personalCardId, CustomUserDetails user) {
    String sql = """
        UPDATE personal_collection_library
        SET tags = array_append(COALESCE(tags, ARRAY[]::text[]), ?)
        WHERE id = ? AND user_id = ?
        """;
    jdbcTemplate.update(sql, tag, personalCardId, user.getId());
    return getUpdatedCardTags(personalCardId, user);
  }

  @Override
  public List<OwnedCard> getAllPersonalLibraryCardsForUser(UUID userId) {
    var pageSize = 12;

    String sql = """
         SELECT\s
             personal_collection_library.id AS personal_library_id,
             personal_collection_library.user_id,
             personal_collection_library.date_added,
             personal_collection_library.updated_at,
             personal_collection_library.tags,
             cards.id AS card_id,
             cards.name,
             cards.type_line,
             cards.toughness,
             cards.power,
             cards.artist,
             cards.cmc,
             cards.scryfall_uri,
             cards.color_identity,
             cards.multiverse_ids,
             cards.image_uris,
             cards.prices
         FROM cards
         INNER JOIN personal_collection_library\s
             ON personal_collection_library.card_id = cards.id
         WHERE personal_collection_library.user_id = ?
         ORDER BY personal_collection_library.date_added DESC , personal_collection_library.id DESC \s
         LIMIT ?
        \s""";

    List<Object> args = new ArrayList<>();
    args.add(userId);
    args.add(pageSize);

    return jdbcTemplate.query(sql, ownedCardRowMapper, args.toArray());
  }

  @Override
  public List<OwnedCard> getAllPersonalLibraryCardsForUser(UUID userId, LibraryFilters personalLibraryFilters) {
    var pageSize = 12;
    int page = personalLibraryFilters.getPage() != null ? Math.max(personalLibraryFilters.getPage(), 0) : 0;

    String limitClause = "LIMIT ? OFFSET ?";

    String nameFilter = (personalLibraryFilters.getCardName() == null || personalLibraryFilters.getCardName().isEmpty())
            ? ""
            : "AND cards.name ILIKE ? ";
    // In your SQL filter builder:
    String typeFilter = (personalLibraryFilters.getCardType() == null
            || personalLibraryFilters.getCardType().isEmpty()
            || "ALL".equalsIgnoreCase(personalLibraryFilters.getCardType())) // ✅ catches "All"
            ? ""
            : "AND cards.type_line ILIKE ? ";
    String colorFilter = (personalLibraryFilters.getSelectedColors() == null
            || personalLibraryFilters.getSelectedColors().isEmpty())
            ? ""
            : "AND cards.color_identity @> ?::text[] AND ?::text[] @> cards.color_identity ";

    String sql = """
         SELECT\s
             personal_collection_library.id AS personal_library_id,
             personal_collection_library.user_id,
             personal_collection_library.date_added,
             personal_collection_library.updated_at,
             personal_collection_library.tags,
             cards.id AS card_id,
             cards.name,
             cards.type_line,
             cards.toughness,
             cards.power,
             cards.artist,
             cards.cmc,
             cards.scryfall_uri,
             cards.color_identity,
             cards.multiverse_ids,
             cards.image_uris,
             cards.prices
         FROM cards
         INNER JOIN personal_collection_library\s
             ON personal_collection_library.card_id = cards.id
         WHERE personal_collection_library.user_id = ?
         %s
         %s
         %s
         AND cards.cmc BETWEEN ? AND ?
         ORDER BY personal_collection_library.date_added DESC , personal_collection_library.id DESC 
         %s
        \s""";

    sql = String.format(sql, nameFilter, typeFilter, colorFilter, limitClause);

    List<Object> args = new ArrayList<>();
    args.add(userId);

    if (personalLibraryFilters.getCardName() != null && !personalLibraryFilters.getCardName().isEmpty()) {
      args.add(personalLibraryFilters.getCardName() + "%");
    }
    if (personalLibraryFilters.getCardType() != null && !personalLibraryFilters.getCardType().isEmpty()
            && !"ALL".equalsIgnoreCase(personalLibraryFilters.getCardType())) {
      args.add("%" + CardType.fromString(personalLibraryFilters.getCardType()).getType() + "%");
    }
    if (personalLibraryFilters.getSelectedColors() != null && !personalLibraryFilters.getSelectedColors().isEmpty()) {
      String[] colorsArray = personalLibraryFilters.getSelectedColors().toArray(new String[0]);
      args.add(colorsArray);
      args.add(colorsArray);
    }

    args.add(personalLibraryFilters.getMinCMC());
    args.add(personalLibraryFilters.getMaxCMC());
    args.add(pageSize);
    args.add(page * pageSize);

    return jdbcTemplate.query(sql, ownedCardRowMapper, args.toArray());
  }

  @Override
  public void addCardToPersonalLibrary(OwnedCard ownedCard) {
    String sql = """
        INSERT INTO personal_collection_library (user_id, card_id, image, date_added, updated_at)
        VALUES (:userId, :cardId, :image, :dateAdded, :updatedAt)
        """;

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("userId", ownedCard.getUserId());
    params.addValue("cardId", ownedCard.getCardId());
    params.addValue("image", ownedCard.getImage());
    params.addValue("dateAdded", ownedCard.getDateAdded());
    params.addValue("updatedAt", ownedCard.getUpdatedAt());

    jdbcClient.sql(sql)
            .paramSource(params)
            .update();
  }

  @Override
  public Map<UUID, List<String>> getDeckLocationsOfCards(CustomUserDetails user, List<UUID> cardIds) {
    if (cardIds == null || cardIds.isEmpty()) {
      return Map.of();
    }

    String sql = """
        SELECT dce.personal_library_card_id, deck.name
        FROM deck_card_entries dce
        JOIN decks deck ON deck.id = dce.deck_id
        WHERE deck.user_id = :userId
        AND dce.personal_library_card_id IN (:cardIds)
        ORDER BY dce.personal_library_card_id, deck.name;
          \s""";

    MapSqlParameterSource params = new MapSqlParameterSource();
    params.addValue("userId", user.getId());
    params.addValue("cardIds", cardIds);

    return jdbcClient.sql(sql)
            .paramSource(params)
            .query((rs, rowNum) -> Map.entry(
                    rs.getObject("personal_library_card_id", UUID.class),
                    rs.getString("name")))
            .list()
            .stream()
            .collect(Collectors.groupingBy(
                    Map.Entry::getKey,
                    Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
  }

  @Override
  public PersonalLibraryStats getStatsOfPersonalLibrary(CustomUserDetails user) {
    String sql = """
         SELECT\s
             personal_collection_library.id AS personal_library_id,
             personal_collection_library.user_id,
             personal_collection_library.date_added,
             personal_collection_library.updated_at,
             personal_collection_library.tags,
             cards.id AS card_id,
             cards.name,
             cards.type_line,
             cards.toughness,
             cards.power,
             cards.artist,
             cards.cmc,
             cards.scryfall_uri,
             cards.color_identity,
             cards.multiverse_ids,
             cards.image_uris,
             cards.prices
         FROM cards
         INNER JOIN personal_collection_library\s
             ON personal_collection_library.card_id = cards.id
         WHERE personal_collection_library.user_id = ?
         ORDER BY personal_collection_library.date_added , personal_collection_library.id\s
        \s""";

    List<OwnedCard> cards = jdbcTemplate.query(sql, ownedCardRowMapper, user.getId());

    double totalValue = cards.stream()
            .filter(card -> card.getCard() != null && card.getCard().getPrices() != null)
            .filter(card -> card.getCard().getPrices().getUsd() != null)
            .mapToDouble(card -> card.getCard().getPrices().getUsd())
            .sum();

    var colorCounts = cards.stream()
            .collect(Collectors.groupingBy(ColorIdentity::fromString, Collectors.counting()));
    var totalCards = cards.size();
    var avgPrice = totalCards == 0 ? 0.0 : totalValue / totalCards;

    return new PersonalLibraryStats(totalValue, colorCounts, totalCards, avgPrice);
  }

}
