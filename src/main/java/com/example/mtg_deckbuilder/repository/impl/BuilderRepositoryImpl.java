package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.mapper.OwnedCardRowMapper;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.repository.api.BuilderRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class BuilderRepositoryImpl implements BuilderRepository {

    private final JdbcTemplate jdbcTemplate;
    private final OwnedCardRowMapper ownedCardRowMapper;

    public BuilderRepositoryImpl(JdbcTemplate jdbcTemplate, OwnedCardRowMapper ownedCardRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.ownedCardRowMapper = ownedCardRowMapper;
    }

    @Override
    public List<OwnedCard> getAllCardsForUser(UUID userId) {
        String sql = """
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
                    cards.multiverse_ids,
                    cards.image_uris,
                    cards.prices
                FROM cards
                INNER JOIN personal_collection_library
                    ON personal_collection_library.card_id = cards.id
                WHERE personal_collection_library.user_id = ?
                ORDER BY cards.name ASC
                """;

        return jdbcTemplate.query(sql, ownedCardRowMapper, userId);
    }
}
