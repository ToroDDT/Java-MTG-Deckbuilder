package com.example.mtg_deckbuilder.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.UUID;


@Setter
@Getter
public class DeckCardEntry {
    @Column("id")
    private UUID id;

    @Column("deck_id")
    private UUID deckId;

    @Column("card_id")
    private UUID cardId;

    @Column("is_sideboard")
    private boolean isSideboard;

    @Column("personal_library_card_id")
    private UUID personalCollectionLibraryId;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;
}
