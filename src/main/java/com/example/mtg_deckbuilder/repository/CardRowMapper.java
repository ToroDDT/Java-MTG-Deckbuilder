package com.example.mtg_deckbuilder.repository;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.model.CardPrices;
import com.example.mtg_deckbuilder.model.ImageUris;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class CardRowMapper {

    private final ObjectMapper objectMapper;

    @Autowired
    public CardRowMapper(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }


    public void extractFields(ResultSet rs, Card card) throws SQLException {
        card.setId(rs.getObject("id", UUID.class));
        card.setName(rs.getString("name"));
        card.setTypeLine(rs.getString("type_line"));
        card.setToughness(rs.getString("toughness"));
        card.setPower(rs.getString("power"));
        card.setArtist(rs.getString("artist"));
        card.setCmc(rs.getInt("cmc"));

        String[] colors = extractColorIdentity(rs);
        List<String> colorList = List.of(colors);
        card.setColorIdentity(colorList);
        card.setColors(colorList);

        card.setMultiverseIds(extractMultiverseIds(rs));
        card.setImage(extractArtCrop(rs));
        card.setPrices(extractCardPrices(rs));
    }

    private String extractArtCrop(ResultSet rs) throws SQLException {
        String raw = rs.getString("image_uris");
        if (raw == null || raw.isBlank()) return null;
        try {
            return objectMapper.readValue(raw, ImageUris.class).getBorderCrop();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private CardPrices extractCardPrices(ResultSet rs) throws SQLException {
        String raw = rs.getString("prices");

        if (raw == null || raw.isBlank()) {
            return CardPrices.builder()
                    .tix(0.0).eurFoil(0.0).usdFoil(0.0).usd(0.0)
                    .build();
        }

        try {
            CardPrices cardPrices = objectMapper.readValue(raw, CardPrices.class);

            if (cardPrices.getUsd() == null) {
                // If both are null, it still remains null, which is fine
                // because we handle that in the UI or set to 0.0 below
                cardPrices.setUsd(cardPrices.getUsdFoil() != null ? cardPrices.getUsdFoil() : 0.0);
            }

            if (cardPrices.getUsdFoil() == null) cardPrices.setUsdFoil(0.0);
            if (cardPrices.getEurFoil() == null) cardPrices.setEurFoil(0.0);
            if (cardPrices.getTix() == null) cardPrices.setTix(0.0);

            return cardPrices; // Essential: Return the processed object

        } catch (Exception e) {
            // Return a zeroed-out builder on parse error rather than null
            return CardPrices.builder().tix(0.0).eurFoil(0.0).usdFoil(0.0).usd(0.0).build();
        }
    }

    private Integer[] extractMultiverseIds(ResultSet rs) throws SQLException {
        Array array = rs.getArray("multiverse_ids");
        if (array == null) return new Integer[0];
        try {
            return (Integer[]) array.getArray();
        } finally {
            array.free();
        }
    }

private String[] extractColorIdentity(ResultSet rs) throws SQLException {
    Array array = rs.getArray("color_identity");
    if (array == null) return new String[0];

    try {
        // .replaceAll("[^a-zA-Z]", "") removes EVERYTHING that isn't a letter
        // This nukes { } [ ] , and spaces in one go.
        String cleanString = array.toString().replaceAll("[^a-zA-Z]", "");
        System.out.println("DEBUG RAW STRING: " + array.toString());
        return cleanString.chars()
                .mapToObj(c -> String.valueOf((char) c))
                .toArray(String[]::new);
    } finally {
        array.free();
    }
}
}