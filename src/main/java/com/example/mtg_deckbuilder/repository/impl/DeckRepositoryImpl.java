package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.model.CardEntry;
import com.example.mtg_deckbuilder.model.Deck;
import com.example.mtg_deckbuilder.model.NewDeck;
import com.example.mtg_deckbuilder.repository.api.DeckRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDate;

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

    /**
     * Retrieves a list of all decks associated with the specified user.
     *
     * @param user the user whose decks are to be retrieved
     * @return a list of {@code Deck} objects of the user's decks
     */
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
    //
    @Override
    public Map<UUID, Double> getDeckTotalPricesForUser(UUID userId) {
        String sql = """
                SELECT d.id,
                    COALESCE(SUM(
                        COALESCE(
                            NULLIF(c.prices->>'usd', '')::numeric,
                            NULLIF(c.prices->>'usd_foil', '')::numeric,
                            NULLIF(c.prices->>'usd_etched', '')::numeric,
                            0
                        )
                    ), 0)::double precision AS total_price
                FROM decks d
                LEFT JOIN deck_card_entries dce ON dce.deck_id = d.id
                LEFT JOIN cards c ON c.id = dce.card_id
                WHERE d.user_id = :userId
                GROUP BY d.id
                """;

        Map<UUID, Double> prices = new HashMap<>();
        jdbcClient.sql(sql)
                .param("userId", userId)
                .query((rs, rowNum) -> {
                    prices.put(rs.getObject("id", UUID.class), rs.getDouble("total_price"));
                    return null;
                })
                .list();
        return prices;
    }

    @Override
    public void updateDeckMetadata(
            CustomUserDetails user,
            UUID deckId,
            String name,
            String commander,
            String colorIdentity,
            String image,
            LocalDate lastUpdate) {
        String sql = """
                UPDATE decks
                SET name = :name,
                    commander = :commander,
                    colors_identity = :colorIdentity,
                    image = :image,
                    last_updated = :lastUpdate
                WHERE id = :id AND user_id = :userId
                """;

        jdbcClient.sql(sql)
                .param("name", name)
                .param("commander", commander)
                .param("colorIdentity", colorIdentity)
                .param("image", image)
                .param("lastUpdate", lastUpdate)
                .param("id", deckId)
                .param("userId", user.getId())
                .update();
    }

    @Override
    public List<Deck> getDeckIds(CustomUserDetails user) {
        String sql = """
        SELECT id, name FROM decks
        WHERE user_id = :userId
        """;

        return jdbcClient.sql(sql)
                .param("userId", user.getId())
                .query((rs, rowNum) -> Deck.builder()
                        .name(rs.getString("name"))
                        .id(rs.getObject("id", UUID.class))
                        .build())
                .list();
    }

    @Override
    public void addCard(CardEntry request) {
        String sql = """
        INSERT INTO deck_card_entries (deck_id, card_id, is_sideboard, personal_library_card_id, owned)
        VALUES (:deckId, :cardId, :isSideboard, :personal_library_card_id, :owned)
        ON CONFLICT (personal_library_card_id)
        DO UPDATE SET
            deck_id = EXCLUDED.deck_id,
            card_id = EXCLUDED.card_id,
            is_sideboard = EXCLUDED.is_sideboard
        """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("deckId", request.deckId())
                .addValue("cardId", request.cardId())
                .addValue("isSideboard", request.isSideboard())
                .addValue("owned", request.owned())
                .addValue("personal_library_card_id", request.personalLibraryCardId());
        jdbcClient.sql(sql)
                .paramSource(params)
                .update();
    }

    @Override
    public void removeDeckEntryByPersonalLibraryCardId(CustomUserDetails user, UUID personalLibraryCardId) {
        String sql = """
                DELETE FROM deck_card_entries AS dce
                USING decks AS d
                WHERE dce.deck_id = d.id
                  AND d.user_id = :userId
                  AND dce.personal_library_card_id = :personalLibraryCardId
                """;
        jdbcClient.sql(sql)
                .param("userId", user.getId())
                .param("personalLibraryCardId", personalLibraryCardId)
                .update();
    }

    @Override
    public void removeDeckEntry(CustomUserDetails user, UUID deckId, UUID deckEntryId) {
        String sql = """
                DELETE FROM deck_card_entries AS dce
                USING decks AS d
                WHERE dce.deck_id = d.id
                  AND d.user_id = :userId
                  AND d.id = :deckId
                  AND dce.id = :deckEntryId
                """;
        jdbcClient.sql(sql)
                .param("userId", user.getId())
                .param("deckId", deckId)
                .param("deckEntryId", deckEntryId)
                .update();
    }
}
