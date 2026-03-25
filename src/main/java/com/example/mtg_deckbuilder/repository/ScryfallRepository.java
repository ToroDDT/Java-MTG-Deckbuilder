package com.example.mtg_deckbuilder.repository;

import com.example.mtg_deckbuilder.model.Card;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ScryfallRepository implements CardRepository {

  private final JdbcClient jdbcClient;
  private final JdbcTemplate jdbcTemplate;

  public ScryfallRepository(JdbcClient jdbcClient, JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcClient = jdbcClient;
  }

  @Override
  public Optional<Card> findById(UUID id) {
    return jdbcClient.sql("select * from cards where id = :id")
        .param("id", id)
        .query(Card.class)
        .optional();
  }

  @Override
  public Optional<Card> findByName(String name) {
    return jdbcTemplate.query(
            "select * from cards where name = ?",
            new CardRowMapper(),
            name
    ).stream().findFirst();
  }

  public Optional<Card> findByColorIdentity(String name) {
    // Note the escaped double quotes around "colorIdentity"
    return jdbcClient.sql("SELECT DISTINCT ON (\"color_identity\") * FROM cards WHERE name = :name")
        .param("name", name)
        .query(Card.class)
        .optional();
  }

  public List<Card> findByCardsBySubstring(String name) {
    String sql = "SELECT * FROM cards WHERE name ILIKE CONCAT('%', :name, '%')";
    return jdbcClient.sql(sql)
        .param("name", name)
        .query(Card.class)
        .list();
  }

  public List<String> findLegalCommanderCards() {
    String sql = """
        SELECT name FROM cards
        WHERE type_line ILIKE '%Legendary%'
          AND type_line ILIKE '%Creature%'
        """;
    return jdbcTemplate.queryForList(sql, String.class);
  }

  public List<Card> executeComplexQuery(String sql, Map<String, ?> params) {
    return jdbcClient.sql(sql)
        .params(params)
        .query(Card.class)
        .list();
  }
}
