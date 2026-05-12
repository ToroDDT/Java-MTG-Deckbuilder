package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.dto.CardCombos;
import com.example.mtg_deckbuilder.repository.api.ComboRespository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class ComboRepositoryImpl implements ComboRespository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;


    public ComboRepositoryImpl(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public void saveCombos(CustomUserDetails ownerId, CardCombos cardCombos) throws JsonProcessingException {
        String sql = "INSERT INTO combos (combo_owner, description, card_combinations, images) " +
                "VALUES (?::uuid, ?::text[], ?::jsonb, ?::jsonb)";

        String[] descArray = cardCombos.getDescription().toArray(new String[0]);
        String cardJson = objectMapper.writeValueAsString(cardCombos.getCardCombinations());
        String imageJson = objectMapper.writeValueAsString(cardCombos.getImages());

        jdbcTemplate.update(sql, ownerId.getId().toString(), descArray, cardJson, imageJson);
    }
    @Override
    public CardCombos getCombos(CustomUserDetails owner) {
        String sql = "SELECT * FROM combos WHERE combo_owner = ?::uuid";

        List<List<String>> allCards = new ArrayList<>();
        List<String> allDesc = new ArrayList<>();
        List<List<String>> allImages = new ArrayList<>();

        jdbcTemplate.query(sql, rs -> {
            Array descArray = rs.getArray("description");
            allDesc.addAll(Arrays.asList((String[]) descArray.getArray()));

            String cardJson = rs.getString("card_combinations");
            String imageJson = rs.getString("images");

            List<List<String>> cards = null;
            try {
                cards = objectMapper.readValue(cardJson,
                        new TypeReference<List<List<String>>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            List<List<String>> images = null;
            try {
                images = objectMapper.readValue(imageJson,
                        new TypeReference<List<List<String>>>() {});
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            allCards.addAll(cards);
            allImages.addAll(images);

        }, owner.getId().toString());

        return CardCombos.builder()
                .description(allDesc)
                .cardCombinations(allCards)
                .images(allImages)
                .build();
    }

}
