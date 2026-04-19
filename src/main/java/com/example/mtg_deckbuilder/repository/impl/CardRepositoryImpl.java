package com.example.mtg_deckbuilder.repository.impl;

import com.example.mtg_deckbuilder.mapper.ScryfallCardRowMapper;
import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.repository.api.CardRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CardRepositoryImpl implements CardRepository {

  private final JdbcClient jdbcClient;
  private final JdbcTemplate jdbcTemplate;
  private final ScryfallCardRowMapper scryfallCardRowMapper;

  public CardRepositoryImpl(ScryfallCardRowMapper scryfallCardRowMapper, JdbcClient jdbcClient, JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcClient = jdbcClient;
    this.scryfallCardRowMapper = scryfallCardRowMapper;
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
            scryfallCardRowMapper,
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

  public List<Card> getCards(String sortingOrder, UUID lastId) {
    String operator = "ASC".equalsIgnoreCase(sortingOrder) ? ">" : "<";
    String direction = "ASC".equalsIgnoreCase(sortingOrder) ? "ASC" : "DESC";
    var pageSize = 12;

    String sql = """
        SELECT name, id
        FROM cards
        WHERE id %s :id
        ORDER BY id %s
        LIMIT :limit
        """;

    // Format the structural SQL (operator and direction)
    sql = String.format(sql, operator, direction);

    return jdbcClient.sql(sql)
            .param("id", lastId)
            .param("limit", pageSize) // Restrict the result set size
            .query(Card.class)
            .list();
  }
}


