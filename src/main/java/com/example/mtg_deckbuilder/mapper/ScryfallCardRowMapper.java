package com.example.mtg_deckbuilder.mapper;

import com.example.mtg_deckbuilder.dto.card.Card;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ScryfallCardRowMapper implements RowMapper<Card> {

    public ScryfallCardRowMapper() {}

    @Override
    public Card mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        return Card.builder()
                .build();
    }
}