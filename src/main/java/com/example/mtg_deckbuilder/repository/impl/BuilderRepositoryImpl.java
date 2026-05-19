package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.dto.card.ImageUris;
import com.example.mtg_deckbuilder.dto.card.Prices;
import com.example.mtg_deckbuilder.model.OwnedCard;
import com.example.mtg_deckbuilder.repository.api.BuilderRepository;
import com.example.mtg_deckbuilder.views.BuilderCardHoverView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class BuilderRepositoryImpl implements BuilderRepository {

    private final JdbcClient jdbcClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public BuilderRepositoryImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public Optional<BuilderCardHoverView> findDeckEntryHover(UUID userId, UUID deckId, UUID deckCardEntryId) {
        String sql = """
                SELECT\s
                  cards.name AS card_name,\s
                  cards.image_uris,\s
                  cards.prices,\s
                  pcl.tags\s
                FROM deck_card_entries dce\s
                INNER JOIN decks d ON d.id = dce.deck_id AND d.user_id = :userId AND d.id = :deckId\s
                INNER JOIN cards ON cards.id = dce.card_id\s
                LEFT JOIN personal_collection_library pcl ON pcl.id = dce.personal_library_card_id\s
                WHERE dce.id = :dceId\s
                """;
        return jdbcClient.sql(sql)
                .param("userId", userId)
                .param("deckId", deckId)
                .param("dceId", deckCardEntryId)
                .query(this::mapHoverRow)
                .optional();
    }

    private BuilderCardHoverView mapHoverRow(ResultSet rs, int rowNum) throws SQLException {
        String name = rs.getString("card_name");
        String imageUrl = largeImageUrlFrom(rs.getString("image_uris"));
        String price = usdPriceFrom(rs.getString("prices"));
        List<String> tags = tagsFromRs(rs.getArray("tags"));
        return new BuilderCardHoverView(name, imageUrl, price, tags);
    }

    private String usdPriceFrom(String raw) {
        if (raw == null || raw.isBlank()) {
            return "$0.00";
        }
        try {
            Prices prices = objectMapper.readValue(raw, Prices.class);
            Double usd = prices.getUsd();
            if (usd == null || usd <= 0) {
                usd = prices.getUsdFoil();
            }
            if (usd == null || usd <= 0) {
                usd = prices.getUsdEtched();
            }
            if (usd == null || usd < 0) {
                return "$0.00";
            }
            return "$" + BigDecimal.valueOf(usd).setScale(2, RoundingMode.HALF_UP);
        } catch (JsonProcessingException e) {
            return "$0.00";
        }
    }

    private static List<String> tagsFromRs(Array sqlArray) throws SQLException {
        if (sqlArray == null) {
            return List.of();
        }
        try {
            String[] raw = (String[]) sqlArray.getArray();
            if (raw == null || raw.length == 0) {
                return List.of();
            }
            return List.copyOf(Arrays.asList(raw));
        } finally {
            sqlArray.free();
        }
    }

    private String largeImageUrlFrom(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            ImageUris u = objectMapper.readValue(raw, ImageUris.class);
            if (u.getLarge() != null && !u.getLarge().isBlank()) {
                return u.getLarge();
            }
            if (u.getNormal() != null && !u.getNormal().isBlank()) {
                return u.getNormal();
            }
            if (u.getPng() != null && !u.getPng().isBlank()) {
                return u.getPng();
            }
            return u.getBorderCrop();
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<Map<String, String>> getAllCardsForUser(String deckId) {
        String sql = """
    SELECT\s
        deck_card_entries.id AS deck_entry_id,

        deck_card_entries.card_id,
        deck_card_entries.personal_library_card_id,

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
   \s""";


        return jdbcClient.sql(sql)
                .param(UUID.fromString(deckId))
                .query((rs, rowNum) -> {
                    Map<String, String> row = new HashMap<>();
                    row.put("deck_entry_id", rs.getObject("deck_entry_id", UUID.class).toString());
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


    @Override
    public List<OwnedCard> getAllCardsFromDeck(UUID deckId) {
        String sql = """
    SELECT\s
        deck_card_entries.id AS deck_entry_id,

        deck_card_entries.card_id,
        deck_card_entries.personal_library_card_id,

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
   \s""";

       return jdbcClient.sql(sql)
    .param(deckId)
    .query((rs, rowNum) -> {
        String imageUrisJson = rs.getString("image_uris");
        return OwnedCard.builder()
            .card(
                Card.builder()
                    .id(rs.getObject("card_id", UUID.class))
                    .name(rs.getString("card_name"))
                    .typeLine(rs.getString("type_line"))
                    .scryfallUri(rs.getString("scryfall_uri"))
                    .imageUris(imageUrisJson)
                    .image(Card.bestArtUrlFromImageUrisJson(imageUrisJson))
                    .build()
            )
            .build();
    })
    .list();
    }

 }
