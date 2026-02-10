package com.example.mtg_deckbuilder.service;
import com.example.mtg_deckbuilder.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CardServiceTest {

    private CardService cardService; // Injects the mock repo into the real service

    @BeforeEach
    void setUp() {
        // We pass null because findByCmc doesn't use the repository
        cardService = new CardService(null);
    }

    @Test
    void testFindByCmc_ValidString() {
        StringBuilder sql = new StringBuilder("SELECT * FROM card WHERE 1=1");
        Map<String, Object> params = new HashMap<>();
        String input = ">5";

        cardService.findByCmc(sql, params, input);

        String finalSql = sql.toString();

        assertTrue(finalSql.contains("SELECT * FROM card WHERE 1=1 AND cmc > :cmc"), "SQL should look like this");

        assertTrue(params.containsKey("cmc"));
        assertEquals(5, params.get("cmc"));
    }

    @Test
    void testFindByColorIdentity() {
        StringBuilder sql = new StringBuilder("SELECT * FROM card WHERE 1=1");
        Map<String, Object> params = new HashMap<>();
        String input = ">5";

        cardService.findByManaCost(sql, params, input);

        String finalSql = sql.toString();

        assertTrue(finalSql.contains("SELECT * FROM card WHERE 1=1 AND "), "SQL should look like this");

    }
}
