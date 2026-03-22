package com.example.mtg_deckbuilder.repository;

import com.example.mtg_deckbuilder.model.AddCardToDeckRequest;
import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.DeckCardEntry;
import com.example.mtg_deckbuilder.model.NewDeck;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository class responsible for managing operations related to the "deck" table in the database.
 * Provides methods for creating and persisting deck entries in the database.
 * It relies on the {@code JdbcClient} for database interactions.
 */
@Repository
public class DeckRepository {
    private final JdbcClient jdbcClient;

    public DeckRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void createNewDeckEntry(NewDeck newDeck) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = """
        INSERT INTO decks (
            user_id, name, format, commander, visibility,\s
            folder, description, colors_identity, last_updated, bracket, url, image
        )\s
        VALUES (
            :userId, :name, :format, :commander, :visibility,\s
            :folder, :description, :colorIdentity, :lastUpdate, :bracket, :url, :image
        )
   \s""";

        jdbcClient.sql(sql)
                .paramSource(new BeanPropertySqlParameterSource(newDeck)) // Passing the object here
                .update(keyHolder, "id"); // Tell JDBC to retrieve the generated "id" column

        newDeck.setId(keyHolder.getKeyAs(UUID.class));
    }


    public List<Deck> getAllDecksForUser(UUID userId) {
        String sql = """
        SELECT * FROM decks
        WHERE user_id = :userId
        """;

        return jdbcClient.sql(sql)
                .param("userId", userId)
                .query(Deck.class)
                .list();
    }
    public void addCardToDeck(AddCardToDeckRequest request) {
        String sql = """
            INSERT INTO deck_card_entries (deck_id, card_id, is_sideboard, personal_library_card_id)
            VALUES (:deckId, :cardId, :isSideboard, :personal_library_card_id)
            RETURNING *
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("deckId", request.deckId())
                .addValue("cardId", request.cardId())
                .addValue("isSideboard", request.isSideboard())
                .addValue("personal_library_card_id", request.personalLibraryCardId());

        jdbcClient.sql(sql)
                .paramSource(params)
                .query(DeckCardEntry.class)
                .single();
    }
}
