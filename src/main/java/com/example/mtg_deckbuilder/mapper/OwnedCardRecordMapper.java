package com.example.mtg_deckbuilder.mapper;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.model.OwnedCard;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.springframework.stereotype.Component;

import java.sql.Array;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Component
public class OwnedCardRecordMapper implements RecordMapper<Record, OwnedCard> {

    @Override
    public OwnedCard map(Record r) {

        Card card = new Card();

        card.setId(r.get("card_id", UUID.class));
        card.setName(r.get("name", String.class));
        card.setTypeLine(r.get("type_line", String.class));
        card.setToughness(r.get("toughness", String.class));
        card.setPower(r.get("power", String.class));
        card.setArtist(r.get("artist", String.class));
        card.setCmc(r.get("cmc", Integer.class));
        card.setScryfallUri(r.get("scryfall_uri", String.class));

        String[] colorIdentity = r.get("color_identity", String[].class);
        card.setColorIdentity(colorIdentity == null ? List.of() : List.of(colorIdentity));

        Array sqlArray = r.get("tags", Array.class);

        List<String> tags;
        try {
            tags = (sqlArray == null)
                    ? List.of()
                    : List.of((String[]) sqlArray.getArray());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read tags array", e);
        }

        UUID personalLibraryId = r.get("personal_library_id", UUID.class);
        UUID cardId = r.get("card_id", UUID.class);

        OffsetDateTime odt = r.get("date_added", OffsetDateTime.class);

        LocalDate dateAdded = odt == null
                ? null
                : odt.atZoneSameInstant(ZoneId.of("UTC")).toLocalDate();

        return new OwnedCard(card, personalLibraryId, cardId, dateAdded, tags);
    }
}