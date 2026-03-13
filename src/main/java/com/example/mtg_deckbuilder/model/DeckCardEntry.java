package com.example.mtg_deckbuilder.model;

import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDateTime;
import java.util.UUID;


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
    public DeckCardEntry() {}

    public DeckCardEntry(UUID id, UUID deckId, UUID cardId, boolean isSideboard, UUID personalCollectionLibraryId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.deckId = deckId;
        this.cardId = cardId;
        this.isSideboard = isSideboard;
        this.personalCollectionLibraryId = personalCollectionLibraryId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getDeckId() { return deckId; }
    public void setDeckId(UUID deckId) { this.deckId = deckId; }

    public UUID getCardId() { return cardId; }
    public void setCardId(UUID cardId) { this.cardId = cardId; }

    public boolean isSideboard() { return isSideboard; }
    public void setSideboard(boolean sideboard) { isSideboard = sideboard; }

    public UUID getPersonalCollectionLibraryId() { return personalCollectionLibraryId; }
    public void setPersonalCollectionLibraryId(UUID personalCollectionLibraryId) { this.personalCollectionLibraryId = personalCollectionLibraryId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
