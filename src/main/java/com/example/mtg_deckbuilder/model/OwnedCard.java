package com.example.mtg_deckbuilder.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;

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
    private String imageId;
    @Column("created_at")
    private String createdAt;
    @Column("updated_at")
    private String updatedAt;

    public OwnedCard() {
    }
}
