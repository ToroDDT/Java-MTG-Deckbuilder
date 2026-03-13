package com.example.mtg_deckbuilder.repository;

import com.example.mtg_deckbuilder.model.Card;
import org.springframework.jdbc.core.RowMapper;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;


public class CardRowMapper implements RowMapper<Card> {
    @Override
    public Card mapRow(ResultSet rs, int rowNum) throws SQLException {
        Card card = new Card();
        card.setName(rs.getString("name"));
        card.setId(rs.getObject("id", java.util.UUID.class));
        if (rs.getArray("multiverse_ids") != null) {
            Array multiverseIdsAsArray = rs.getArray("multiverse_ids");
            Integer[] multiverseIds = (Integer[]) multiverseIdsAsArray.getArray();
            card.setMultiverseIds(multiverseIds);
        } else {
            System.out.println("Casting failed. Not a String.");
        }
        return card;
    }
}
