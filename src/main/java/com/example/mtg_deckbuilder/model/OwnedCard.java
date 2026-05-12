package com.example.mtg_deckbuilder.model;

import com.example.mtg_deckbuilder.model.cards.ScryfallCardObject;
import lombok.*;
import org.jooq.Record; // Import jOOQ Record
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.jooq.generated.Tables.CARDS;
import static com.example.jooq.generated.Tables.PERSONAL_COLLECTION_LIBRARY;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OwnedCard {
    private UUID id;
    private UUID userId;
    private UUID cardId;
    private String image;
    private LocalDate dateAdded;
    private LocalDate updatedAt;
    private String name;
    private String type;
    private List<String> colors;
    private List<String> tags;
    private List<String> deckLocations = new ArrayList<>();
    private ScryfallCardObject card;

    public OwnedCard() {}

    public static OwnedCard mapFromRecord(Record record) {
        var cardObject = ScryfallCardObject.builder()
                .id(record.get(CARDS.ID))
                .name(record.get(CARDS.NAME))
                .colorIdentity(record.get(CARDS.COLOR_IDENTITY))
                .cmc(record.get(CARDS.CMC))
                .typeLine(record.get(CARDS.TYPE_LINE))
                .artist(record.get(CARDS.ARTIST))
                .build();

        return OwnedCard.builder()
                .card(cardObject)
                // Use the alias "personal_library_id" if you use .as() in your SQL,
                // otherwise use PERSONAL_COLLECTION_LIBRARY.ID
                .id(record.get("personal_library_id", UUID.class))
                .cardId(record.get(PERSONAL_COLLECTION_LIBRARY.CARD_ID))
                .dateAdded(record.get(PERSONAL_COLLECTION_LIBRARY.DATE_ADDED))
                .tags(record.get(PERSONAL_COLLECTION_LIBRARY.TAGS))
                .build();
    }

    public static class OwnedCardBuilder {
        public OwnedCardBuilder tags(String[] tags) {
            this.tags = Optional.ofNullable(tags).map(List::of).orElseGet(List::of);
            return this;
        }

        public OwnedCardBuilder dateAdded(OffsetDateTime dateTime) {
            this.dateAdded = Optional.ofNullable(dateTime)
                    .map(dt -> dt.atZoneSameInstant(ZoneId.of("UTC")).toLocalDate())
                    .orElse(null);
            return this;
        }
    }
}