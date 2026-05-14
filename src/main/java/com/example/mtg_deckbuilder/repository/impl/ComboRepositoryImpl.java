package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.dto.combo.CardCombos;
import com.example.mtg_deckbuilder.repository.api.ComboRepository;
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
public class ComboRepositoryImpl implements ComboRepository{

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;


    public ComboRepositoryImpl(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }
   @Override
public void saveCombos(CustomUserDetails owner, CardCombos cardCombos) throws JsonProcessingException {
    // 1. Define the SQL with the UPSERT (ON CONFLICT) logic
    String sql = "INSERT INTO combos (combo_owner, location, description, card_combinations, images) " +
                 "VALUES (?::uuid, ?, ?::text[], ?::jsonb, ?::jsonb) " +
                 "ON CONFLICT (combo_owner, location) " +
                 "DO UPDATE SET " +
                 "description = EXCLUDED.description, " +
                 "card_combinations = EXCLUDED.card_combinations, " +
                 "images = EXCLUDED.images";

    // 2. Prepare the data
    String ownerUuid = owner.getId().toString();
    String deckLocation = cardCombos.getLocation(); // This maps to the 'location' column
    String[] descArray = cardCombos.getDescription().toArray(new String[0]);
    String cardJson = objectMapper.writeValueAsString(cardCombos.getCardCombinations());
    String imageJson = objectMapper.writeValueAsString(cardCombos.getImages());

    // 3. Execute with EXACT parameter order
    jdbcTemplate.update(sql,
        ownerUuid,    // 1. combo_owner (?::uuid)
        deckLocation, // 2. location (?)
        descArray,    // 3. description (?::text[])
        cardJson,     // 4. card_combinations (?::jsonb)
        imageJson     // 5. images (?::jsonb)
    );
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

            List<List<String>> cards;
            try {
                cards = objectMapper.readValue(cardJson,
                        new TypeReference<>() {
                        });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            List<List<String>> images;
            try {
                images = objectMapper.readValue(imageJson,
                        new TypeReference<>() {
                        });
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