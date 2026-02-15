package com.example.mtg_deckbuilder.repository;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.utils.Inequality;
import com.example.mtg_deckbuilder.utils.Parser;
import com.example.mtg_deckbuilder.utils.InequalityParser;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class CardRepository {
    private final JdbcClient jdbcClient;
    private final Parser<Inequality> queryParser = new InequalityParser();

    public CardRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<Card> findById(UUID id) {
        return jdbcClient.sql("select * from cards where id = :id")
                .param("id", id) // Safely binds the UUID
                .query(Card.class) // Auto-maps to your Card record
                .optional(); // Returns Optional<Card> automatically
    }

    public Optional<Card> findByName(String name) {
        return jdbcClient.sql("select * from cards where name = :name")
                .param("name", name) // Safely binds the UUID
                .query(Card.class) // Auto-maps to your Card record
                .optional(); // Returns Optional<Card> automatically
    }

    public List<Card> findByCardsBySubstring(String name) {
        String sql = "SELECT * FROM cards WHERE name ILIKE CONCAT('%', :name, '%')";
        return jdbcClient.sql(sql)
                .param("name", name)
                .query(Card.class)
                .list(); // Returns a List of <Cards> else returns []
    }

    public List<Card> executeComplexQuery(String sql, Map<String, Object> params) {
        return jdbcClient.sql(sql)
                .params(params)
                .query(Card.class)
                .list();
    }
}
