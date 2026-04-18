package com.example.mtg_deckbuilder.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OwnedCard {
    private UUID id;
    @Column("user_id")
    private UUID userId;
    @Column("card_id")
    private UUID cardId;
    @Column("image_id")
    private String image;
    @Column("date_added")
    private LocalDate dateAdded;
    @Column("updated_at")
    private LocalDate updatedAt;
    private String name;
    private String type;
    private List<String> colors;
    @Column
    private List<String> tags;
    private Card card;

    public OwnedCard() {}

    public OwnedCard(Card card, UUID id, UUID cardId) {
        this.card = card;
        this.id = id;
        this.cardId = cardId;
    }
}
