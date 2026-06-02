package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.exceptions.CardDoesNotExistException;
import com.example.mtg_deckbuilder.mapper.OwnedCardRowMapper;
import com.example.mtg_deckbuilder.model.*;
import com.example.mtg_deckbuilder.repository.api.PersonalLibraryRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.views.api.PersonalLibraryStats;
import lombok.NonNull;
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
    private final OwnedCardRowMapper rowMapper;

    public PersonalLibraryRepositoryImpl(OwnedCardRowMapper rowMapper, JdbcClient jdbcClient,
                                         JdbcTemplate jdbcTemplate) {
        this.jdbcClient = jdbcClient;
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }


    public void deleteCard(@NonNull CustomUserDetails user, @NonNull UUID personalCardId) {
        String sql = """
        DELETE FROM personal_collection_library
        WHERE id = :personalCardId AND user_id = :userId
        """;

        var rowsChanged = jdbcClient.sql(sql)
                .param("personalCardId", personalCardId)
                .param("userId", user.getId())
                .update();

        if (rowsChanged == 0) {
            throw new CardDoesNotExistException("Card could not be found in library:" + personalCardId);
        }

    }
    private List<String> getUpdatedCardTags(@NonNull UUID personalCardId, @NonNull CustomUserDetails user) {
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
    public List<String> saveTags(String tag, UUID personalCardId, CustomUserDetails user) {
        String sql = """
        UPDATE personal_collection_library
        SET tags = array_append(COALESCE(tags, ARRAY[]::text[]), ?)
        WHERE id = ? AND user_id = ?
        """;
        jdbcTemplate.update(sql, tag, personalCardId, user.getId());
        return getUpdatedCardTags(personalCardId, user);
    }

    @Override
    public List<String> deleteTag(String tag, UUID personalCardId, CustomUserDetails user) {
        String sql = """
        UPDATE personal_collection_library
        SET tags = array_remove(tags, ?)
        WHERE id = ? AND user_id = ?
        """;
        jdbcTemplate.update(sql, tag, personalCardId, user.getId());
        return getUpdatedCardTags(personalCardId, user);
    }

    @Override
    public List<OwnedCard> findCards(UUID userId) {

        String sql = """
        SELECT
            pcl.id AS personal_library_id,
            pcl.user_id,
            pcl.date_added,
            pcl.updated_at,
            pcl.tags,

            c.id AS card_id,
            c.name,
            c.type_line,
            c.toughness,
            c.power,
            c.artist,
            c.cmc,
            c.scryfall_uri,
            c.color_identity,
            c.image_uris,
            c.card_faces,

            COALESCE(
                c.image_uris->>'border_crop',
                c.card_faces->0->'image_uris'->>'border_crop'
            ) AS image,

            c.prices->>'usd' AS usd,
            c.prices->>'usd_foil' AS usd_foil,
            c.prices->>'eur_foil' AS eur_foil,
            c.prices->>'tix' AS tix

        FROM personal_collection_library pcl

        JOIN cards c
            ON c.id = pcl.card_id

        WHERE pcl.user_id = ?

        ORDER BY
            pcl.date_added DESC,
            pcl.id DESC
        """;

        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    @Override
    public List<OwnedCard> findCardsForCombos(UUID userId) {

        String sql = """
        SELECT
            pcl.id AS personal_library_id,
            pcl.user_id,
            pcl.date_added,
            pcl.updated_at,
            pcl.tags,

            c.id AS card_id,
            c.name,
            c.type_line,
            c.toughness,
            c.power,
            c.artist,
            c.cmc,
            c.scryfall_uri,
            c.color_identity,
            c.image_uris,
            c.card_faces,

            COALESCE(
                c.image_uris->>'border_crop',
                c.card_faces->0->'image_uris'->>'border_crop'
            ) AS image,

            c.prices->>'usd' AS usd,
            c.prices->>'usd_foil' AS usd_foil,
            c.prices->>'eur_foil' AS eur_foil,
            c.prices->>'tix' AS tix

        FROM personal_collection_library pcl

        JOIN cards c
            ON c.id = pcl.card_id

        WHERE pcl.user_id = ?

        ORDER BY
            pcl.date_added DESC,
            pcl.id DESC
        """;

        return jdbcTemplate.query(sql, rowMapper, userId);
    }

    @Override
    public List<OwnedCard> findCardsPaginated(UUID userId) {

        int pageSize = 12;

        String sql = """
        SELECT
            pcl.id AS personal_library_id,
            pcl.user_id,
            pcl.date_added,
            pcl.updated_at,
            pcl.tags,

            c.id AS card_id,
            c.name,
            c.type_line,
            c.toughness,
            c.power,
            c.artist,
            c.cmc,
            c.scryfall_uri,
            c.color_identity,
            c.image_uris,
            c.card_faces,

            COALESCE(
                c.image_uris->>'border_crop',
                c.card_faces->0->'image_uris'->>'border_crop'
            ) AS image,

            c.prices->>'usd' AS usd,
            c.prices->>'usd_foil' AS usd_foil,
            c.prices->>'eur_foil' AS eur_foil,
            c.prices->>'tix' AS tix

        FROM personal_collection_library pcl

        JOIN cards c
            ON c.id = pcl.card_id

        WHERE pcl.user_id = ?

        ORDER BY
            pcl.date_added DESC,
            pcl.id DESC

        LIMIT ?
        """;

        return jdbcTemplate.query(
                sql,
                rowMapper,
                userId,
                pageSize
        );
    }

    @Override
    public List<OwnedCard> findCards(
            UUID userId,
            LibraryFilters personalLibraryFilters
    ) {

        int pageSize = 12;

        int page = personalLibraryFilters.getPage() != null
                ? Math.max(personalLibraryFilters.getPage(), 0)
                : 0;

        StringBuilder sql = new StringBuilder("""
        SELECT
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
            cards.image_uris,
            cards.card_faces,

            COALESCE(
                cards.image_uris->>'border_crop',
                cards.card_faces->0->'image_uris'->>'border_crop'
            ) AS image,

            cards.prices->>'usd' AS usd,
            cards.prices->>'usd_foil' AS usd_foil,
            cards.prices->>'eur_foil' AS eur_foil,
            cards.prices->>'tix' AS tix

        FROM cards

        INNER JOIN personal_collection_library
            ON personal_collection_library.card_id = cards.id

        WHERE personal_collection_library.user_id = :userId
        """);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", userId);

        // Oracle / rules text: English FTS drops many tokens; NULL oracle_text yields NULL tsvector.
        // Use `simple` + substring match so queries like "flying", "draw", "destroy" reliably hit rules text.
        if (personalLibraryFilters.getOracleTextSearch() != null
                && !personalLibraryFilters.getOracleTextSearch().isEmpty()) {

            String oracleQuery = personalLibraryFilters.getOracleTextSearch().trim();

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

        // Card name
        if (personalLibraryFilters.getCardName() != null
                && !personalLibraryFilters.getCardName().isEmpty()) {

            sql.append("""
            AND cards.name ILIKE :cardName
            """);

            params.addValue(
                    "cardName",
                    personalLibraryFilters.getCardName() + "%"
            );
        }

        // Card type
        if (personalLibraryFilters.getCardType() != null
                && !personalLibraryFilters.getCardType().isEmpty()
                && !"ALL".equalsIgnoreCase(personalLibraryFilters.getCardType())) {

            sql.append("""
            AND cards.type_line ILIKE :cardType
            """);

            params.addValue(
                    "cardType",
                    "%" + CardType
                            .fromString(personalLibraryFilters.getCardType())
                            .getType() + "%"
            );
        }

        // Colors
        if (personalLibraryFilters.getSelectedColors() != null
                && !personalLibraryFilters.getSelectedColors().isEmpty()) {

            sql.append("""
            AND cards.color_identity @> :colors::text[]
            AND :colors::text[] @> cards.color_identity
            """);

            params.addValue(
                    "colors",
                    personalLibraryFilters
                            .getSelectedColors()
                            .toArray(new String[0])
            );
        }

        // Tags
        var tagTokens = personalLibraryFilters.tagSearchTokens();

        for (int i = 0; i < tagTokens.size(); i++) {

            String paramName = "tag" + i;

            sql.append("""
            AND EXISTS (
                SELECT 1
                FROM unnest(
                    COALESCE(
                        personal_collection_library.tags,
                        ARRAY[]::text[]
                    )
                ) AS tag_row(tag_value)
                WHERE tag_value ILIKE :%s
            )\s
           \s""".formatted(paramName));

            params.addValue(
                    paramName,
                    "%" + tagTokens.get(i) + "%"
            );
        }

        // CMC range
        sql.append("""
        AND cards.cmc BETWEEN :minCmc AND :maxCmc
        """);

        params.addValue("minCmc", personalLibraryFilters.getMinCMC());
        params.addValue("maxCmc", personalLibraryFilters.getMaxCMC());

        // Sorting
        switch (personalLibraryFilters.getSortBy()) {

            case PRICE_ASC -> sql.append("""
            ORDER BY
                NULLIF(cards.prices->>'usd', '')::numeric ASC NULLS LAST
            """);

            case PRICE_DESC -> sql.append("""
            ORDER BY
                NULLIF(cards.prices->>'usd', '')::numeric DESC NULLS LAST
            """);

            case CMC_ASC -> sql.append("""
            ORDER BY cards.cmc ASC NULLS LAST
            """);

            case CMC_DESC -> sql.append("""
            ORDER BY cards.cmc DESC NULLS LAST
            """);

            case NAME_DESC -> sql.append("""
            ORDER BY cards.name DESC
            """);

            default -> sql.append("""
            ORDER BY
                personal_collection_library.date_added DESC,
                personal_collection_library.id DESC
            """);
        }

        // Pagination
        sql.append("""
        LIMIT :limit
        OFFSET :offset
        """);

        params.addValue("limit", pageSize);
        params.addValue("offset", page * pageSize);

        return jdbcClient
                .sql(sql.toString())
                .params(params.getValues())
                .query(rowMapper)
                .list();
    }

    @Override
    public Boolean findCardExists(UUID userId, String cardId) {
        String sql = """
            SELECT EXISTS (
                SELECT 1\s
                FROM personal_collection_library\s
                WHERE user_id = :userId AND card_id = :cardId
            )
           \s""";

        return jdbcClient.sql(sql)
                .param("userId", userId)
                .param("cardId", UUID.fromString(cardId)) // Convert String to UUID safely
                .query(Boolean.class)
                .optional()
                .orElse(false);
    }

    @Override
    public void saveCard(OwnedCard ownedCard) {
        String sql = """
        INSERT INTO personal_collection_library (user_id, card_id, image)
        VALUES (:userId, :cardId, :image)
        """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", ownedCard.getUserId());
        params.addValue("cardId", ownedCard.getCardId());
        params.addValue("image", ownedCard.getImage());


        jdbcClient.sql(sql)
                .paramSource(params)
                .update();
    }
    @Override
    public Map<UUID, List<String>> findLocations(CustomUserDetails user, List<UUID> cardIds) {
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
    public List<OwnedCard> getInfo(CustomUserDetails user) {
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
             cards.image_uris,
             cards.card_faces,
             COALESCE(
                 cards.image_uris->>'border_crop',
                 cards.card_faces->0->'image_uris'->>'border_crop'
             ) AS image,
             cards.prices->>'usd' AS usd,
             cards.prices->>'usd_foil' AS usd_foil,
             cards.prices->>'eur_foil' AS eur_foil,
             cards.prices->>'tix' AS tix
         FROM cards
         INNER JOIN personal_collection_library\s
             ON personal_collection_library.card_id = cards.id
         WHERE personal_collection_library.user_id = ?
         ORDER BY personal_collection_library.date_added DESC , personal_collection_library.id DESC \s
        \s""";

        return jdbcTemplate.query(sql, rowMapper, user.getId());

    }

}
