package com.example.mtg_deckbuilder.mapper;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.model.OwnedCard;
import org.jspecify.annotations.NonNull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

        Array sqlArray = rs.getArray("tags");
        List<String> tags = sqlArray == null
                ? List.of()
                : Arrays.asList((String[]) sqlArray.getArray());

        UUID personalLibraryId = rs.getObject("personal_library_id", UUID.class);
        UUID cardId = rs.getObject("card_id", UUID.class);
        LocalDate dateAdded = rs.getObject("date_added", OffsetDateTime.class)
                .atZoneSameInstant(ZoneId.of("UTC"))  // or your app's zone
                .toLocalDate();
        return new OwnedCard(card, personalLibraryId, cardId, dateAdded, tags);
    }
}
