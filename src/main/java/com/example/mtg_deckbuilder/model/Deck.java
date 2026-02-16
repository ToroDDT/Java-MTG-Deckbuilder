package com.example.mtg_deckbuilder.model;

import java.time.LocalDate;
import java.util.List;

public record Deck(Long id,
                   String name,
                   List<String> colors,
                   String format,
                   Integer bracket,
                   LocalDate lastUpdated,
                   String url) {}
