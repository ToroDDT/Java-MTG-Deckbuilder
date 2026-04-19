package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.mapper.OwnedCardRowMapper;
import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.repository.api.PersonalLibraryRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
public List<OwnedCard> getAllPersonalLibraryCardsForUser(UUID userId, String lastId) {
    var pageSize = 12;
    var sortingOrder = "ASC";

    String operator = "ASC".equalsIgnoreCase(sortingOrder) ? ">" : "<";
    String direction = "ASC".equalsIgnoreCase(sortingOrder) ? "ASC" : "DESC";

    // 1. Build the dynamic WHERE clause
    String paginationFilter = (lastId == null || lastId.isEmpty())
            ? ""
            : "AND personal_collection_library.id " + operator + " ? ";

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
        ORDER BY personal_collection_library.id %s
        LIMIT ?
       \s""";

    sql = String.format(sql, paginationFilter, direction);

    List<Object> args = new ArrayList<>();
    args.add(userId);

    if (lastId != null && !lastId.isEmpty()) {
        args.add(lastId);
    }

    args.add(pageSize);

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
}
