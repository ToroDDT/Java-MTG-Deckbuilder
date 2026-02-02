package com.example.mtg_deckbuilder.repository;

import com.example.mtg_deckbuilder.model.Card;
import com.example.mtg_deckbuilder.model.CardType;
import com.example.mtg_deckbuilder.utils.Inequality;
import com.example.mtg_deckbuilder.utils.InequalityMatcher;
import com.example.mtg_deckbuilder.utils.QueryParser;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.*;


@Repository
public class CardRepository {
    private final JdbcClient jdbcClient;
    private final QueryParser queryParser = new QueryParser();

    public CardRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<Card> findById(UUID id) {
        return jdbcClient.sql("select * from card where id = :id")
                .param("id", id) // Safely binds the UUID
                .query(Card.class) // Auto-maps to your Card record
                .optional(); // Returns Optional<Card> automatically
    }

    public Optional<Card> findByName(String name) {
        return jdbcClient.sql("select * from card where name = :name")
                .param("name", name) // Safely binds the UUID
                .query(Card.class) // Auto-maps to your Card record
                .optional(); // Returns Optional<Card> automatically
    }

    public List<Card> findByCardsBySubstring(String name) {
        String sql = "SELECT * FROM card WHERE name ILIKE CONCAT('%', :name, '%')";
        return jdbcClient.sql(sql)
                .param("name", name)
                .query(Card.class)
                .list(); // Returns a List of <Cards> else returns []
    }

    private void findByTypeLine(StringBuilder sql, String typeLine, Map<String, Object> params) {
        CardType type = CardType.fromString(typeLine); // Returns CardType.SORCERY
        switch(type) {
            case BATTLE:
                sql.append(" AND WHERE type_line = :type");
                params.put("type", "Battle");
                break;
            case SORCERY:
                sql.append(" AND WHERE type_line = :type");
                params.put("type", "Sorcery");
                break;
            case CREATURE:
                sql.append(" AND WHERE type_line = :type");
                params.put("type", "Creature");
                break;
            case ENCHANTMENT:
                sql.append(" AND WHERE type_line = :type");
                params.put("type", "Enchantment");
                break;
            case INSTANT:
                sql.append(" AND WHERE type_line = :type");
                params.put("type", "Instant");
                break;
            case LAND:
                sql.append(" AND WHERE type_line = :type");
                params.put("type", "Land");
                break;
            case PLANESWALKER:
                sql.append(" AND WHERE type_line = :type");
                params.put("type", "Planeswalker");
                break;
            case KINDRED:
                sql.append(" AND WHERE type_line = :type");
                params.put("type", "Kindred");
                break;
            case PLANE:
                sql.append(" AND WHERE type_line = :type");
                params.put("type", "Plane");
                break;
            case null:
                break;
        }
    }

    private void findByCmc(StringBuilder sql, String cmcInput, Map<String, Object> params) {
        Inequality expression = queryParser.parseInequality(cmcInput);
        if (expression == null) return;

        // Whitelist the operator to be 100% safe
        String safeOp = switch (expression.operator()) {
            case ">", "<", ">=", "<=", "=" -> expression.operator();
            default -> "=";
        };

        String query = " AND WHERE cmc " + safeOp + " :cmc";
        sql.append(query);
        params.put("cmc", expression.value());
    }

   public List<Card> findCardByComplexQuery(String name, String cmc, String power) {
        // 1. Start with the base query
        StringBuilder sql = new StringBuilder("SELECT * FROM card WHERE 1=1");
        Map<String, Object> params = new HashMap<>();

        // 2. Dynamically append filters
        if (name != null && !name.isBlank()) {
            sql.append(" AND name ILIKE CONCAT('%', :name, '%')");
            params.put("name", name);
        }

        if (cmc != null && !cmc.isBlank()) {
           findByCmc(sql, cmc, params);
        }

        // 3. Execute with the params map
        return jdbcClient.sql(sql.toString())
                .params(params)
                .query(Card.class)
                .list();
    }

}
