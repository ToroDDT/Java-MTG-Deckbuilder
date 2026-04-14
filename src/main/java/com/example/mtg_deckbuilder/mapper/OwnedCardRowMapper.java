package com.example.mtg_deckbuilder.mapper;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.model.OwnedCard;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class OwnedCardRowMapper implements  RowMapper<OwnedCard> {
    private final CardRowMapper cardRowMapper;

    public OwnedCardRowMapper(CardRowMapper cardRowMapper) {
        this.cardRowMapper = cardRowMapper;
    }

    @Override
    public OwnedCard mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        Card card = new Card();
        cardRowMapper.extractFields(rs, card);
        return new OwnedCard(card);
    }
}
