package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.dto.combo.CardCombos;
import com.example.mtg_deckbuilder.dto.combo.ComboVariant;
import com.example.mtg_deckbuilder.repository.api.ComboRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class ComboRepositoryImpl implements ComboRepository{

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;


    public ComboRepositoryImpl(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public void saveCombos(CustomUserDetails owner, CardCombos cardCombos) throws JsonProcessingException {
        String sql = "INSERT INTO combos (combo_owner, location, description, card_combinations, images, results, variants) " +
                     "VALUES (?::uuid, ?, ?::text[], ?::jsonb, ?::jsonb, ?::text[], ?::jsonb) " +
                     "ON CONFLICT (combo_owner, location) " +
                     "DO UPDATE SET " +
                     "description = EXCLUDED.description, " +
                     "card_combinations = EXCLUDED.card_combinations, " +
                     "images = EXCLUDED.images, " +
                     "results = EXCLUDED.results, " +
                     "variants = EXCLUDED.variants";

        String ownerUuid = owner.getId().toString();
        String deckLocation = cardCombos.getLocation();
        String[] descArray = cardCombos.getDescription().toArray(new String[0]);
        String cardJson = objectMapper.writeValueAsString(cardCombos.getCardCombinations());
        String imageJson = objectMapper.writeValueAsString(cardCombos.getImages());
        String[] resultArray = (cardCombos.getResults() == null ? List.<String>of() : cardCombos.getResults()).toArray(new String[0]);
        String variantsJson = objectMapper.writeValueAsString(
                cardCombos.getVariants() == null ? List.of() : cardCombos.getVariants());

        jdbcTemplate.update(sql,
                ownerUuid,
                deckLocation,
                descArray,
                cardJson,
                imageJson,
                resultArray,
                variantsJson
        );
    }

    @Override
    public CardCombos getCombos(CustomUserDetails owner) {
        String sql = "SELECT * FROM combos WHERE combo_owner = ?::uuid";

        List<List<String>> allCards = new ArrayList<>();
        List<String> allDesc = new ArrayList<>();
        List<List<String>> allImages = new ArrayList<>();
        List<String> allLocations = new ArrayList<>();
        List<String> allResults = new ArrayList<>();
        List<ComboVariant> allVariants = new ArrayList<>();

        RowCallbackHandler comboRowHandler = rs -> {
            Array descArray = rs.getArray("description");
            String[] descriptions = (String[]) descArray.getArray();

            String cardJson = rs.getString("card_combinations");
            String imageJson = rs.getString("images");
            String location = rs.getString("location");
            Array resultArray = rs.getArray("results");
            String variantsJson = hasColumn(rs, "variants") ? rs.getString("variants") : null;

            List<List<String>> cards = readNestedStringList(cardJson);
            List<List<String>> images = readNestedStringList(imageJson);
            List<String> results = readResults(resultArray, cards.size());
            List<ComboVariant> variants = readVariants(variantsJson, cards.size());

            allDesc.addAll(Arrays.asList(descriptions));
            allCards.addAll(cards);
            allImages.addAll(images);
            for (int i = 0; i < cards.size(); i++) {
                allLocations.add(location);
                allResults.add(i < results.size() ? results.get(i) : "");
                allVariants.add(i < variants.size() ? variants.get(i) : null);
            }

        };

        jdbcTemplate.query(sql, comboRowHandler, owner.getId().toString());

        return CardCombos.builder()
                .description(allDesc)
                .cardCombinations(allCards)
                .images(allImages)
                .results(allResults)
                .locations(allLocations)
                .variants(allVariants)
                .build();
    }

    private List<ComboVariant> readVariants(String json, int comboCount) {
        if (json == null || json.isBlank()) {
            return new ArrayList<>(java.util.Collections.nCopies(comboCount, null));
        }

        try {
            List<ComboVariant> variants = objectMapper.readValue(json, new TypeReference<>() {});
            if (variants.size() >= comboCount) {
                return variants;
            }
            List<ComboVariant> padded = new ArrayList<>(variants);
            while (padded.size() < comboCount) {
                padded.add(null);
            }
            return padded;
        } catch (JsonProcessingException e) {
            return new ArrayList<>(java.util.Collections.nCopies(comboCount, null));
        }
    }

    private List<List<String>> readNestedStringList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> readResults(Array resultArray, int comboCount) {
        if (resultArray == null) {
            return new ArrayList<>(java.util.Collections.nCopies(comboCount, ""));
        }

        try {
            String[] values = (String[]) resultArray.getArray();
            return values == null ? new ArrayList<>(java.util.Collections.nCopies(comboCount, "")) : new ArrayList<>(Arrays.asList(values));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            if (columnName.equalsIgnoreCase(meta.getColumnName(i))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> getLocations(CustomUserDetails owner) {
        String sql = """
                SELECT location
                FROM combos
                WHERE combo_owner = ?::uuid
                  AND location IS NOT NULL
                  AND btrim(location) <> ''
                ORDER BY location
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getString("location"),
                owner.getId().toString()
        );
    }

}
