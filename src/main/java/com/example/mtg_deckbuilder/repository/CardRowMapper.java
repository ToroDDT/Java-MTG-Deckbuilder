package com.example.mtg_deckbuilder.repository;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.model.ImageUris;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.jdbc.core.RowMapper;
import tools.jackson.databind.ObjectMapper;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CardRowMapper implements RowMapper<Card> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(); // shared, not per-row

    @Override
    public Card mapRow(ResultSet rs, int rowNum) throws SQLException {
        Card card = new Card();
        card.setName(rs.getString("name"));
        card.setId(rs.getObject("id", UUID.class));
        card.setMultiverseIds(extractMultiverseIds(rs));
        card.setImage(extractArtCrop(rs));
        return card;
    }

    private Integer[] extractMultiverseIds(ResultSet rs) throws SQLException {
        Array array = rs.getArray("multiverse_ids");
        return array != null ? (Integer[]) array.getArray() : new Integer[0];
    }

    private String extractArtCrop(ResultSet rs) throws SQLException {
        String raw = rs.getString("image_uris");
        if (raw == null) return null;
        try {
            return OBJECT_MAPPER.readValue(raw, ImageUris.class).getArtCrop();
        } catch (Exception e) {
            throw new SQLException("Failed to deserialize image_uris", e);
        }
    }
}