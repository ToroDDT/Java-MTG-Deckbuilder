package com.example.mtg_deckbuilder.repository;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.model.CardPrices;
import com.example.mtg_deckbuilder.model.ImageUris;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class CardRowMapper {
    public static void extractFields(ResultSet rs, Card card) throws SQLException {
        card.setName(rs.getString("name"));
        card.setId(rs.getObject("id", UUID.class));
        card.setMultiverseIds(extractMultiverseIds(rs));
        card.setImage(extractArtCrop(rs));
        card.setColorIdentity(List.of(extractColorIdentity(rs)));
        card.setColors(List.of(extractColorIdentity(rs)));
        card.setTypeLine(rs.getString("type_line"));
        card.setCmc(BigDecimal.valueOf(rs.getInt("cmc")));
        card.setToughness(rs.getString("toughness"));
        card.setPower(rs.getString("power"));
        card.setArtist(rs.getString("artist"));
        card.setPrices(extractCardPrices(rs));
    }

    private static Integer[] extractMultiverseIds(ResultSet rs) throws SQLException {
        Array array = rs.getArray("multiverse_ids");
        return array != null ? (Integer[]) array.getArray() : new Integer[0];
    }
    private static String[] extractColorIdentity(ResultSet rs) throws SQLException {
        Array array = rs.getArray("color_identity");
        return array != null ? (String []) array.getArray() : new String[0];
    }

    private static String extractArtCrop(ResultSet rs) throws SQLException {
        String raw = rs.getString("image_uris");
        if (raw == null) return null;
        try {
            return new ObjectMapper().readValue(raw, ImageUris.class).getBorderCrop();
        } catch (Exception e) {
            throw new SQLException("Failed to deserialize image_uris", e);
        }
    }
    private static CardPrices extractCardPrices(ResultSet rs) throws SQLException {
        String raw = rs.getString("prices");
        if (raw == null) return null;
        try {
            return new ObjectMapper().readValue(raw, CardPrices.class);
        } catch (Exception e){
            throw new SQLException("Failed to deserialize prices", e);
        }
    }
}
