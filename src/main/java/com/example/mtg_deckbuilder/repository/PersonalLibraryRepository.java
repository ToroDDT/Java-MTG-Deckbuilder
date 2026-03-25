package com.example.mtg_deckbuilder.repository;

import com.example.mtg_deckbuilder.model.OwnedCard;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.util.List;
import java.util.UUID;

public class PersonalLibraryRepository {
    private final JdbcClient jdbcClient;

    public PersonalLibraryRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<OwnedCard> getAllPersonalLibraryCardsForUser (UUID userId) {
        String sql = """
                select * FROM personal_collection_library where user_id = :userId
                """;
        return jdbcClient.sql(sql)
                .param(userId)
                .query(OwnedCard.class)
                .list();
    }
    public void addCardToPersonalLibrary (OwnedCard ownedCard) {
        String sql = """
                INSERT INTO personal_collection_library (user_id, card_id, image_id, created_at, updated_at)
                VALUES (:userId, :cardId, :imageId, :createdAt, :updatedAt)
                """;

        MapSqlParameterSource  params = new MapSqlParameterSource();
        params.addValue("userId", ownedCard.getUserId());
        params.addValue("cardId", ownedCard.getCardId());
        params.addValue("imageId", ownedCard.getImageId());
        params.addValue("createdAt", ownedCard.getCreatedAt());
        params.addValue("updated_at", ownedCard.getUpdatedAt());

        jdbcClient.sql(sql)
                .paramSource(params);
    }
}
