package com.example.mtg_deckbuilder.mapper;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.model.Prices;
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
card.setId(rs.getObject("card_id", UUID.class)); // instead of "id"
        card.setName(rs.getString("name"));
        card.setTypeLine(rs.getString("type_line"));
        card.setToughness(rs.getString("toughness"));
        card.setPower(rs.getString("power"));
        card.setArtist(rs.getString("artist"));
        card.setCmc(rs.getInt("cmc"));
        card.setScryfallUri(rs.getString("scryfall_uri"));

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

    private Prices extractCardPrices(ResultSet rs) throws SQLException {
        String raw = rs.getString("prices");

        if (raw == null || raw.isBlank()) {
            return Prices.builder()
                    .tix(0.0).eurFoil(0.0).usdFoil(0.0).usd(0.0)
                    .build();
        }

        try {
            Prices prices = objectMapper.readValue(raw, Prices.class);

            if (prices.getUsd() == null) {
                // If both are null, it still remains null, which is fine
                // because we handle that in the UI or set to 0.0 below
                prices.setUsd(prices.getUsdFoil() != null ? prices.getUsdFoil() : 0.0);
            }

            if (prices.getUsdFoil() == null) prices.setUsdFoil(0.0);
            if (prices.getEurFoil() == null) prices.setEurFoil(0.0);
            if (prices.getTix() == null) prices.setTix(0.0);
            return prices; // Essential: Return the processed object

        } catch (Exception e) {
            e.printStackTrace();
            // Return a zeroed-out builder on parse error rather than null
            return Prices.builder().tix(0.0).eurFoil(0.0).usdFoil(0.0).usd(0.0).build();
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
        return cleanString.chars()
                .mapToObj(c -> String.valueOf((char) c))
                .toArray(String[]::new);
    } finally {
        array.free();
    }
}
}