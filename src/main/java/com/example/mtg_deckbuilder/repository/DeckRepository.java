package com.example.mtg_deckbuilder.repository;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.NewDeck;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
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
    /**
     * Creates a new deck entry in the database by inserting the provided {@code NewDeck} object
     * and populates its {@code id} field with the generated value from the database. This is done by
     * {@code GeneratedKeyHolder} which returns the generated key from the database. Which will be later
     * used to set the {@code id} field of the {@code NewDeck} object. This will be used to redirect the user
     * to the newly created deck page.
     *
     * @param newDeck the {@code NewDeck} object to be created and persisted.
     *                It contains information such as name, format, commander, visibility,
     *                folder, description, colors, last update date, and bracket.
     * @return the {@code NewDeck} object with its {@code id} field set to the generated UUID.
     */
    public NewDeck createNewDeckEntry(NewDeck newDeck) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

       String sql = """
        INSERT INTO decks (
            user_id, name, format, commander, visibility,\s
            folder, description, colors_identity, last_updated, bracket, url
        )\s
        VALUES (
            :userId, :name, :format, :commander, :visibility,\s
            :folder, :description, :colorIdentity, :lastUpdate, :bracket, :url
        )
   \s""";

    jdbcClient.sql(sql)
              .paramSource(new BeanPropertySqlParameterSource(newDeck)) // Passing the object here
                .update(keyHolder, "id"); // Tell JDBC to retrieve the generated "id" column

        newDeck.setId(keyHolder.getKeyAs(UUID.class));
        System.out.println("New deck created with id: " + newDeck.getId());
        return newDeck;
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


}
