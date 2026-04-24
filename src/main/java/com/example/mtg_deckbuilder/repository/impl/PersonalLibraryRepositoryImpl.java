package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.mapper.OwnedCardRowMapper;
import com.example.mtg_deckbuilder.model.CardType;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.repository.api.PersonalLibraryRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PersonalLibraryRepositoryImpl implements PersonalLibraryRepository {

    private final JdbcClient jdbcClient;
    private final JdbcTemplate jdbcTemplate;
    private final OwnedCardRowMapper ownedCardRowMapper;

    public PersonalLibraryRepositoryImpl(OwnedCardRowMapper ownedCardRowMapper, JdbcClient jdbcClient, JdbcTemplate jdbcTemplate) {
        this.jdbcClient = jdbcClient;
        this.jdbcTemplate = jdbcTemplate;
        this.ownedCardRowMapper = ownedCardRowMapper;
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
        String operator = (personalLibraryFilters.getOperator() == null || ">".equalsIgnoreCase(personalLibraryFilters.getOperator())) ? ">" : "<";
        String direction = "ASC";

        // Use cursor pagination when browsing, offset when filtering
        String paginationFilter = (!personalLibraryFilters.hasSearchFilter() && personalLibraryFilters.getDateAdded() != null && !personalLibraryFilters.getDateAdded().isEmpty())
                ? "AND personal_collection_library.date_added " + operator + " ? "
                : "";

        String limitClause = personalLibraryFilters.hasSearchFilter()
                ? "LIMIT ? OFFSET ?"
                : "LIMIT ?";

        String nameFilter = (personalLibraryFilters.getCardName() == null || personalLibraryFilters.getCardName().isEmpty())
                ? "" : "AND cards.name ILIKE ? ";
        // In your SQL filter builder:
        String typeFilter = (personalLibraryFilters.getCardType() == null
                || personalLibraryFilters.getCardType().isEmpty()
                || "ALL".equalsIgnoreCase(personalLibraryFilters.getCardType()))  // ✅ catches "All"
                ? "" : "AND cards.type_line ILIKE ? ";
        String colorFilter = (personalLibraryFilters.getSelectedColors() == null || personalLibraryFilters.getSelectedColors().isEmpty())
                ? "" : "AND cards.color_identity @> ?::text[] AND ?::text[] @> cards.color_identity ";

        String sql = """
        SELECT\s
            personal_collection_library.id AS personal_library_id,
            personal_collection_library.user_id,
            personal_collection_library.date_added,
            personal_collection_library.updated_at,
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
        %s
        AND cards.cmc BETWEEN ? AND ?
        ORDER BY personal_collection_library.date_added %s
        %s
       \s""";

        sql = String.format(sql, paginationFilter, nameFilter, typeFilter, colorFilter, direction, limitClause);

        List<Object> args = new ArrayList<>();
        args.add(userId);

        if (!personalLibraryFilters.hasSearchFilter() && personalLibraryFilters.getDateAdded() != null && !personalLibraryFilters.getDateAdded().isEmpty()) {
            args.add(LocalDate.parse(personalLibraryFilters.getDateAdded()));
        }
        if (personalLibraryFilters.getCardName() != null && !personalLibraryFilters.getCardName().isEmpty()) {
            args.add(personalLibraryFilters.getCardName() + "%");
        }
        if (personalLibraryFilters.getCardType() != null && !personalLibraryFilters.getCardType().isEmpty() && !"ALL".equalsIgnoreCase(personalLibraryFilters.getCardType())) {
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

        if (personalLibraryFilters.hasSearchFilter()) {
            int page = personalLibraryFilters.getPage() != null ? personalLibraryFilters.getPage() : 0;
            args.add(page * pageSize); // OFFSET
        }

        return jdbcTemplate.query(sql, ownedCardRowMapper, args.toArray());
    }

    @Override
    public void addCardToPersonalLibrary (OwnedCard ownedCard) {
        String sql = """
                INSERT INTO personal_collection_library (user_id, card_id, image, date_added, updated_at)
                VALUES (:userId, :cardId, :image, :dateAdded, :updatedAt)
                """;

        MapSqlParameterSource  params = new MapSqlParameterSource();
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
    public Map<UUID, List<String>> getDeckLocationsOfCards (CustomUserDetails user, List<UUID> cardIds) {

        String sql = """
            SELECT dce.card_id, deck.name
            FROM deck_card_entries dce
            JOIN decks deck ON deck.id = dce.deck_id
            JOIN personal_collection_library pcl ON pcl.id = dce.personal_library_card_id
            WHERE deck.user_id = :userId
            AND dce.personal_library_card_id IN (:cardIds)
            ORDER BY dce.card_id, deck.name;
              \s""";

        MapSqlParameterSource  params = new MapSqlParameterSource();
        params.addValue("userId", user.getId());
        params.addValue("cardIds", cardIds );

        return jdbcClient.sql(sql)
                .paramSource(params)
                .query((rs, rowNum) -> Map.entry(
                        rs.getObject("card_id", UUID.class),
                        rs.getString("name")
                ))
                .list()
                .stream()
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                ));
    }
}
