package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.mapper.OwnedCardRowMapper;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.repository.api.BuilderRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BuilderRepositoryImpl implements BuilderRepository {

    private final JdbcTemplate jdbcTemplate;
    private final JdbcClient jdbcClient;
    private final OwnedCardRowMapper ownedCardRowMapper;

    public BuilderRepositoryImpl(JdbcTemplate jdbcTemplate, OwnedCardRowMapper ownedCardRowMapper,  JdbcClient jdbcClient) {
        this.jdbcTemplate = jdbcTemplate;
        this.ownedCardRowMapper = ownedCardRowMapper;
        this.jdbcClient = jdbcClient;
    }

@Override
public List<Map<String, String>> getAllCardsForUser(String deckId) {
    String sql = """
            SELECT 
                deck_card_entries.id AS dce_id,
                deck_card_entries.deck_id,
                deck_card_entries.card_id,
                deck_card_entries.personal_library_card_id,
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
            INNER JOIN deck_card_entries
                ON deck_card_entries.card_id = cards.id
            WHERE deck_card_entries.deck_id = ?
            ORDER BY cards.name 
            """;

    return jdbcClient.sql(sql)
            .param(UUID.fromString(deckId))
            .query((rs, rowNum) -> {
                Map<String, String> row = new HashMap<>();
                row.put("name", rs.getString("name"));
                row.put("color_identity", rs.getString("color_identity"));
                row.put("type_line", rs.getString("type_line"));
                // add whatever other fields you need
                return row;
            })
            .list();
}
}
