package com.example.mtg_deckbuilder.mapper;

import com.example.mtg_deckbuilder.model.Card;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ScryfallCardRowMapper implements RowMapper<Card> {
    private final CardRowMapper cardRowMapper;

    public ScryfallCardRowMapper(CardRowMapper cardRowMapper) {
        this.cardRowMapper = cardRowMapper;
    }

    @Override
    public Card mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        Card card = new Card();
        cardRowMapper.extractFields(rs, card);
        return card;
    }
}