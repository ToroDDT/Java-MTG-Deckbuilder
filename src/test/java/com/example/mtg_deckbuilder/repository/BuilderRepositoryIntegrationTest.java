package com.example.mtg_deckbuilder.repository;

import com.example.mtg_deckbuilder.repository.api.BuilderRepository;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static com.example.mtg_deckbuilder.service.impl.PersonalLibraryServiceImplTest.testUser;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
public class BuilderRepositoryIntegrationTest {
    @Autowired
    private BuilderRepository builderRepository;
    @Autowired
    private JdbcClient jdbcClient;
    private final UUID deckId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        CustomUserDetails user = testUser();
        var email = "ddt1999@gmail.com";
        String sql = "INSERT INTO decks (id, name, user_id, format) VALUES (?, ?, ?)";
        String sqlTwo = "INSERT INTO users(id, username, password, email) VALUES (?, ?, ?, ?)";
        jdbcClient.sql(sqlTwo)
                .params(deckId, user.getId(), user.getUsername(), user.getUsername(), email)
                .update();
        jdbcClient.sql(sql)
                .params("Test Deck", user.getId(), "Commander")
                .update();
    }


    @Test
    public void testThrowsException() {
        var cards = builderRepository.getAllCardsForUser(String.valueOf(deckId));

        assertThat(cards).allSatisfy(card -> {

        });
    }
}
