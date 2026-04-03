package com.example.mtg_deckbuilder.repository;

import com.example.mtg_deckbuilder.model.Card;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ScryfallCardRowMapper implements RowMapper<Card> {

    @Override
    public Card mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        Card card = new Card();
        CardRowMapper.extractFields(rs, card);
        return card;
    }
}