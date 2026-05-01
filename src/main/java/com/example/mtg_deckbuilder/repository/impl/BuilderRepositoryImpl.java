package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.model.Prices;
import com.example.mtg_deckbuilder.repository.api.BuilderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BuilderRepositoryImpl implements BuilderRepository {

    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

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
        cards.cmc,
        cards.scryfall_uri,
        cards.color_identity,
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
                    row.put("image", rs.getString("deck_image"));

                    String pricesJson = rs.getString("prices");
                    if (pricesJson != null) {
                        try {
                            Prices prices = objectMapper.readValue(pricesJson, Prices.class);
                            String usd = prices.getUsd().toString();
                            row.put("prices", usd != null ? usd : "0.00");
                        } catch (JsonProcessingException e) {
                            row.put("prices", "0.00");
                        }
                    } else {
                        row.put("prices", "0.00");
                    }
                    return row;
                })
                .list();
    }
}
