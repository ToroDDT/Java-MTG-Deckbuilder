package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.mapper.ScryfallCardRowMapper;
import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.model.CardType;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.repository.api.CardRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CardRepositoryImpl implements CardRepository {

  private static final int PAGE_SIZE = 12;

  private static final String CARD_SELECT = """
        SELECT
            cards.id,
            cards.name,
            cards.type_line,
            cards.toughness,
            cards.power,
            cards.artist,
            cards.cmc,
            cards.scryfall_uri,
            cards.color_identity,
            cards.image_uris,
            cards.card_faces,
            cards.released_at,
            COALESCE(
                cards.image_uris->>'border_crop',
                cards.card_faces->0->'image_uris'->>'border_crop'
            ) AS image,
            cards.prices->>'usd' AS usd,
            cards.prices->>'usd_foil' AS usd_foil,
            cards.prices->>'eur_foil' AS eur_foil,
            cards.prices->>'tix' AS tix
        FROM cards
        """;

  private final JdbcClient jdbcClient;
  private final JdbcTemplate jdbcTemplate;
  private final ScryfallCardRowMapper scryfallCardRowMapper;

  public CardRepositoryImpl(ScryfallCardRowMapper scryfallCardRowMapper, JdbcClient jdbcClient, JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcClient = jdbcClient;
    this.scryfallCardRowMapper = scryfallCardRowMapper;
  }

  @Override
  public Optional<Card> findById(UUID id) {
    return jdbcClient.sql("select * from cards where id = :id")
            .param("id", id)
            .query(Card.class)
            .optional();
  }

  @Override
  public Optional<Card> findByName(String name) {
    return jdbcTemplate.query(
            "select * from cards where name = ?",
            scryfallCardRowMapper,
            name
    ).stream().findFirst();
  }

  public Optional<Card> findByColorIdentity(String name) {
    return jdbcClient.sql("SELECT DISTINCT ON (\"color_identity\") * FROM cards WHERE name = :name")
            .param("name", name)
            .query(Card.class)
            .optional();
  }

  @Override
  public List<Card> findByCardsBySubstring(String name) {
    String sql = "SELECT * FROM cards WHERE name ILIKE CONCAT('%', :name, '%') LIMIT 10";
    return jdbcClient.sql(sql)
            .param("name", name)
            .query(scryfallCardRowMapper)
            .list();
  }

  @Override
  public List<String> findLegalCommanderCards() {
    String sql = """
        SELECT name FROM cards
        WHERE type_line ILIKE '%Legendary%'
          AND type_line ILIKE '%Creature%'
        """;
    return jdbcTemplate.queryForList(sql, String.class);
  }

  @Override
  public List<Card> findCardsPaginated() {
    String sql = CARD_SELECT + """
        ORDER BY cards.released_at DESC NULLS LAST, cards.id DESC
        LIMIT :limit
        """;

    return jdbcClient.sql(sql)
            .param("limit", PAGE_SIZE)
            .query(scryfallCardRowMapper)
            .list();
  }

  @Override
  public List<Card> findCards(LibraryFilters filters) {
    int page = filters.getPage() != null ? Math.max(filters.getPage(), 0) : 0;
    int minCmc = filters.getMinCMC() != null ? filters.getMinCMC() : 0;
    int maxCmc = filters.getMaxCMC() != null ? filters.getMaxCMC() : 16;

    StringBuilder sql = new StringBuilder(CARD_SELECT + " WHERE 1=1 ");
    MapSqlParameterSource params = new MapSqlParameterSource();

    if (filters.getOracleTextSearch() != null && !filters.getOracleTextSearch().isEmpty()) {
      String oracleQuery = filters.getOracleTextSearch().trim();
      sql.append("""
            AND (
                to_tsvector('simple', coalesce(cards.oracle_text, ''))
                    @@ plainto_tsquery('simple', :oracleText)
                OR position(:oracleTextSub in lower(coalesce(cards.oracle_text, ''))) > 0
            )
            """);
      params.addValue("oracleText", oracleQuery);
      params.addValue("oracleTextSub", oracleQuery.toLowerCase(Locale.ROOT));
    }

    if (filters.getCardName() != null && !filters.getCardName().isEmpty()) {
      sql.append(" AND cards.name ILIKE :cardName ");
      params.addValue("cardName", filters.getCardName() + "%");
    }

    if (filters.getCardType() != null
            && !filters.getCardType().isEmpty()
            && !"ALL".equalsIgnoreCase(filters.getCardType())) {
      sql.append(" AND cards.type_line ILIKE :cardType ");
      params.addValue("cardType", "%" + CardType.fromString(filters.getCardType()).getType() + "%");
    }

    if (filters.getSelectedColors() != null && !filters.getSelectedColors().isEmpty()) {
      sql.append("""
            AND cards.color_identity @> :colors::text[]
            AND :colors::text[] @> cards.color_identity
            """);
      params.addValue("colors", filters.getSelectedColors().toArray(new String[0]));
    }

    sql.append(" AND cards.cmc BETWEEN :minCmc AND :maxCmc ");
    params.addValue("minCmc", minCmc);
    params.addValue("maxCmc", maxCmc);

    appendSort(sql, filters.getSortBy());

    sql.append("""
        LIMIT :limit
        OFFSET :offset
        """);
    params.addValue("limit", PAGE_SIZE);
    params.addValue("offset", page * PAGE_SIZE);

    return jdbcClient.sql(sql.toString())
            .params(params.getValues())
            .query(scryfallCardRowMapper)
            .list();
  }

  private static void appendSort(StringBuilder sql, com.example.mtg_deckbuilder.model.SortOptions sortBy) {
    if (sortBy == null) {
      sortBy = com.example.mtg_deckbuilder.model.SortOptions.RECENT;
    }

    switch (sortBy) {
      case PRICE_ASC -> sql.append("""
            ORDER BY NULLIF(cards.prices->>'usd', '')::numeric ASC NULLS LAST,
                     cards.released_at DESC NULLS LAST
            """);
      case PRICE_DESC -> sql.append("""
            ORDER BY NULLIF(cards.prices->>'usd', '')::numeric DESC NULLS LAST,
                     cards.released_at DESC NULLS LAST
            """);
      case CMC_ASC -> sql.append("""
            ORDER BY cards.cmc ASC NULLS LAST,
                     cards.released_at DESC NULLS LAST
            """);
      case CMC_DESC -> sql.append("""
            ORDER BY cards.cmc DESC NULLS LAST,
                     cards.released_at DESC NULLS LAST
            """);
      case NAME_ASC -> sql.append(" ORDER BY cards.name ASC ");
      case NAME_DESC -> sql.append(" ORDER BY cards.name DESC ");
      default -> sql.append("""
            ORDER BY cards.released_at DESC NULLS LAST, cards.id DESC
            """);
    }
  }

  public List<Card> getCards(String sortingOrder, UUID lastId) {
    String operator = "ASC".equalsIgnoreCase(sortingOrder) ? ">" : "<";
    String direction = "ASC".equalsIgnoreCase(sortingOrder) ? "ASC" : "DESC";

    String sql = """
        SELECT name, id
        FROM cards
        WHERE id %s :id
        ORDER BY id %s
        LIMIT :limit
        """.formatted(operator, direction);

    return jdbcClient.sql(sql)
            .param("id", lastId)
            .param("limit", PAGE_SIZE)
            .query(Card.class)
            .list();
  }
}
