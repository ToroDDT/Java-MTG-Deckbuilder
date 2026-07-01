package com.example.mtg_deckbuilder.config;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class ComboDatabaseInitializer {

    private final JdbcTemplate jdbcTemplate;

    public ComboDatabaseInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    void ensureComboVariantsColumn() {
        jdbcTemplate.execute("ALTER TABLE combos ADD COLUMN IF NOT EXISTS variants JSONB");
    }
}
