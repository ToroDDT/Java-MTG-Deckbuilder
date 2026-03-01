package com.example.mtg_deckbuilder.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record Deck(UUID id,
                   String name,
                   String format,
                   String description,
                   String folder,
                   String visibility,
                   String commander,
                   String bracket,
                   String colors_identity,
                   LocalDate last_updated,
                   UUID user_id,
                   String url
                   ) {}
