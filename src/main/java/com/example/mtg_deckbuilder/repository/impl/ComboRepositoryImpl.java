package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.dto.combo.CardCombos;
import com.example.mtg_deckbuilder.repository.api.ComboRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.Array;
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
        String sql = "INSERT INTO combos (combo_owner, location, description, card_combinations, images, results) " +
                     "VALUES (?::uuid, ?, ?::text[], ?::jsonb, ?::jsonb, ?::text[]) " +
                     "ON CONFLICT (combo_owner, location) " +
                     "DO UPDATE SET " +
                     "description = EXCLUDED.description, " +
                     "card_combinations = EXCLUDED.card_combinations, " +
                     "images = EXCLUDED.images, " +
                     "results = EXCLUDED.results";

        String ownerUuid = owner.getId().toString();
        String deckLocation = cardCombos.getLocation();
        String[] descArray = cardCombos.getDescription().toArray(new String[0]);
        String cardJson = objectMapper.writeValueAsString(cardCombos.getCardCombinations());
        String imageJson = objectMapper.writeValueAsString(cardCombos.getImages());
        String[] resultArray = (cardCombos.getResults() == null ? List.<String>of() : cardCombos.getResults()).toArray(new String[0]);

        jdbcTemplate.update(sql,
                ownerUuid,
                deckLocation,
                descArray,
                cardJson,
                imageJson,
                resultArray
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

        RowCallbackHandler comboRowHandler = rs -> {
            Array descArray = rs.getArray("description");
            String[] descriptions = (String[]) descArray.getArray();

            String cardJson = rs.getString("card_combinations");
            String imageJson = rs.getString("images");
            String location = rs.getString("location");
            Array resultArray = rs.getArray("results");

            List<List<String>> cards = readNestedStringList(cardJson);
            List<List<String>> images = readNestedStringList(imageJson);
            List<String> results = readResults(resultArray, cards.size());

            allDesc.addAll(Arrays.asList(descriptions));
            allCards.addAll(cards);
            allImages.addAll(images);
            for (int i = 0; i < cards.size(); i++) {
                allLocations.add(location);
                allResults.add(i < results.size() ? results.get(i) : "");
            }

        };

        jdbcTemplate.query(sql, comboRowHandler, owner.getId().toString());

        return CardCombos.builder()
                .description(allDesc)
                .cardCombinations(allCards)
                .images(allImages)
                .results(allResults)
                .locations(allLocations)
                .build();
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
