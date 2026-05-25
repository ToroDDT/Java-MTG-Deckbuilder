package com.example.mtg_deckbuilder.mappers;

import com.example.mtg_deckbuilder.mapper.OwnedCardRowMapper;
import com.example.mtg_deckbuilder.model.OwnedCard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OwnedCardMapperTest {

    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSetMetaData metaData;

    @Mock
    private Array colorIdentitySqlArray;

    private final OwnedCardRowMapper mapper = new OwnedCardRowMapper();

    @Test
    void shouldMapRowToOwnedCard() throws SQLException {
        String[] columns = { "card_id", "name", "type_line", "toughness", "power", "artist", "cmc", "scryfall_uri", "prices", "color_identity" };

        when(resultSet.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(columns.length);

        for (int i = 0; i < columns.length; i++) {
            int sqlIndex = i + 1; // SQL metadata indices are 1-based
            when(metaData.getColumnLabel(sqlIndex)).thenReturn(columns[i]);
            when(metaData.getColumnName(sqlIndex)).thenReturn(columns[i]);
        }

        // 2. Prepare mock database values
        UUID mockCardId = UUID.randomUUID();
        UUID mockLibraryId = UUID.randomUUID();
        String[] mockColors = {"W", "U"};

        when(resultSet.getArray("tags")).thenReturn(null);
        when(resultSet.getObject("personal_library_id", UUID.class)).thenReturn(mockLibraryId);
        when(resultSet.getObject("date_added", OffsetDateTime.class)).thenReturn(OffsetDateTime.parse("2024-01-15T12:00:00Z"));

        when(resultSet.getObject("card_id", UUID.class)).thenReturn(mockCardId);
        when(resultSet.getString("name")).thenReturn("Black Lotus");
        when(resultSet.getString("type_line")).thenReturn("Artifact");
        when(resultSet.getString("toughness")).thenReturn(null);
        when(resultSet.getString("power")).thenReturn(null);
        when(resultSet.getString("artist")).thenReturn("Christopher Rush");
        when(resultSet.getInt("cmc")).thenReturn(0);
        when(resultSet.getString("scryfall_uri")).thenReturn("https://scryfall.com/mock");

        // Prices block extraction (since "usd" isn't in our column list, it defaults to checking "prices")
        when(resultSet.getString("prices")).thenReturn(null);

        // Color identity extraction
        when(resultSet.getArray("color_identity")).thenReturn(colorIdentitySqlArray);
        when(colorIdentitySqlArray.getArray()).thenReturn(mockColors);
        doNothing().when(colorIdentitySqlArray).free();

        // 4. Act
        OwnedCard result = mapper.mapRow(resultSet, 1);

        // 5. Assert
        assertNotNull(result);
        assertNotNull(result.getCard());
        assertEquals("Black Lotus", result.getCard().getName());
        assertEquals(0.0, result.getCard().getPrices().getUsd());
        assertEquals(2, result.getCard().getColorIdentity().size());
        assertEquals(List.of(), result.getTags());
    }
}