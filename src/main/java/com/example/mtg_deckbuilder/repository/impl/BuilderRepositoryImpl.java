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

    private final JdbcClient jdbcClient;

    public BuilderRepositoryImpl(JdbcClient jdbcClient) {
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
        cards.name AS card_name,
        cards.type_line,
        cards.toughness,
        cards.power,
        cards.artist,
        cards.cmc,
        cards.scryfall_uri,
        cards.color_identity,
        cards.multiverse_ids,
        cards.image_uris,
        cards.prices,

        decks.name AS deck_name,
        decks.commander,
        decks.image AS deck_image

    FROM cards

    INNER JOIN deck_card_entries
        ON deck_card_entries.card_id = cards.id

    INNER JOIN decks
        ON deck_card_entries.deck_id = decks.id

    WHERE deck_card_entries.deck_id = ?
    ORDER BY cards.name
    """;


        return jdbcClient.sql(sql)
                .param(UUID.fromString(deckId))
                .query((rs, rowNum) -> {
                    Map<String, String> row = new HashMap<>();
                    row.put("name", rs.getString("card_name"));
                    row.put("color_identity", rs.getString("color_identity"));
                    row.put("type_line", rs.getString("type_line"));
                    row.put("cmc", rs.getString("cmc"));
                    row.put("deck_name", rs.getString("deck_name"));
                    return row;
                })
                .list();
    }
}
