package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.model.CardEntry;
import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.DeckCardEntry;
import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.repository.api.DeckRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
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
public class DeckRepositoryImpl implements DeckRepository {
    private final JdbcClient jdbcClient;

    public DeckRepositoryImpl(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
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


    @Override
    public List<Deck> getDecks(CustomUserDetails user) {
        String sql = """
        SELECT * FROM decks
        WHERE user_id = :userId
        """;

        return jdbcClient.sql(sql)
                .param("userId", user.getId())
                .query(Deck.class)
                .list();
    }

    @Override
    public List<String> getDeckNames(CustomUserDetails user) {
        String sql = """
        SELECT name FROM decks
        WHERE user_id = :userId
        """;

        return jdbcClient.sql(sql)
                .param("userId", user.getId())
                .query(String.class)
                .list();
    }

@Override
public void addCard(CardEntry request) {
    String sql = """
        INSERT INTO deck_card_entries (deck_id, card_id, is_sideboard, personal_library_card_id)
        VALUES (:deckId, :cardId, :isSideboard, :personal_library_card_id)
        ON CONFLICT (personal_library_card_id)
        DO UPDATE SET deck_id = EXCLUDED.deck_id
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
