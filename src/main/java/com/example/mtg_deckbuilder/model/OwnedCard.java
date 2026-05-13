package com.example.mtg_deckbuilder.model;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
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
    private List<String> deckLocations = new ArrayList<>();
    private Card card;

    public OwnedCard() {}
    public static OwnedCard from(Card card, CustomUserDetails user) {
        return OwnedCard.builder()
                .id(card.getId())
                .cardId(card.getId())
                .userId(user.getId())
                .image(card.getImage())
                .tags(new ArrayList<>()) // Default empty list
                .build();
    }

    public OwnedCard mapRows(ResultSet rs) throws SQLException {

        Array sqlArray = rs.getArray("tags");
        List<String> tags = sqlArray == null
                ? List.of()
                : Arrays.asList((String[]) sqlArray.getArray());

        UUID personalLibraryId = rs.getObject("personal_library_id", UUID.class);
        UUID cardId = rs.getObject("card_id", UUID.class);
        LocalDate dateAdded = rs.getObject("date_added", OffsetDateTime.class)
                .atZoneSameInstant(ZoneId.of("UTC"))  // or your app's zone
                .toLocalDate();
        var card = Card.builder().build().extractFields(rs);

        return OwnedCard.builder()
                .id(personalLibraryId)
                .cardId(cardId)
                .dateAdded(dateAdded)
                .tags(tags)
                .card(card)
                .build();
    }
}
