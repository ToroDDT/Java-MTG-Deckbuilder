package com.example.mtg_deckbuilder.repository;

import com.example.mtg_deckbuilder.model.OwnedCard;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class DefaultPersonalLibraryRepository implements PersonalLibraryRepository{

    private final JdbcClient jdbcClient;
    private final JdbcTemplate jdbcTemplate;
    private final OwnedCardRowMapper ownedCardRowMapper;

    public DefaultPersonalLibraryRepository(OwnedCardRowMapper ownedCardRowMapper, JdbcClient jdbcClient, JdbcTemplate jdbcTemplate) {
        this.jdbcClient = jdbcClient;
        this.jdbcTemplate = jdbcTemplate;
        this.ownedCardRowMapper = ownedCardRowMapper;
    }

    @Override
    public List<OwnedCard> getAllPersonalLibraryCardsForUser (UUID userId) {

        String sql = """
                SELECT *
                FROM cards\s
                INNER JOIN personal_collection_library\s
                ON personal_collection_library.card_id = cards.id
                WHERE personal_collection_library.user_id = ?;
               \s""";

        return jdbcTemplate.query(sql, ownedCardRowMapper, userId)
                .stream()
                .toList();
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
