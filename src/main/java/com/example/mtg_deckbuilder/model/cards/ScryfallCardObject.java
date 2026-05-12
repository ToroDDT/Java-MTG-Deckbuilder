package com.example.mtg_deckbuilder.model.cards;

import com.example.mtg_deckbuilder.model.Prices;
import lombok.*;
import org.jooq.Record;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.jooq.generated.Tables.CARDS;

@Builder
@AllArgsConstructor
@NoArgsConstructor // Added for flexibility
@Getter
@Setter
public class ScryfallCardObject {
    private UUID id;
    private String image;
    private String oracleId;
    private Integer[] multiverseIds;
    private Integer mtgoId;
    private Integer mtgoFoilId;
    private Integer tcgplayerId;
    private Integer tcgplayerEtchedId;
    private Integer cardmarketId;
    private String lang;
    private LocalDate releasedAt;
    private String uri;
    private String scryfallUri;

    // Layout / Image
    private String layout;
    private Boolean highresImage;
    private String imageStatus;
    private String imageUris;

    // Gameplay
    private String name;
    private String manaCost;
    private Integer cmc;
    private String typeLine;
    private String oracleText;
    private String power;
    private String toughness;
    private String loyalty;
    private String defense;
    private List<String> colors;
    private List<String> colorIdentity;
    private List<String> colorIndicator;
    private List<String> keywords;
    private List<String> producedMana;
    private Boolean reserved;
    private Boolean gameChanger;
    private String legalities;
    private String allParts;
    private String cardFaces;

    // Print
    private String artist;
    private List<String> artistIds;
    private String illustrationId;
    private String flavorText;
    private String flavorName;
    private String watermark;
    private String borderColor;
    private String frame;
    private List<String> frameEffects;
    private String securityStamp;
    private Boolean fullArt;
    private Boolean textless;
    private Boolean oversized;
    private Boolean booster;
    private Boolean digital;
    private Boolean foil;
    private Boolean nonfoil;
    private List<String> finishes;
    private List<String> games;
    private Boolean promo;
    private List<String> promoTypes;
    private Boolean reprint;
    private Boolean variation;
    private String variationOf;
    private Boolean storySpotlight;
    private String collectorNumber;
    private String rarity;
    private String cardBackId;

    // Set
    private String setId;
    private String setCode;
    private String setName;
    private String setType;
    private String setUri;
    private String setSearchUri;
    private String scryfallSetUri;
    private String rulingsUri;
    private String printsSearchUri;

    // Rankings
    private Integer edhrecRank;
    private Integer pennyRank;
    private String handModifier;
    private String lifeModifier;

    // Nested jsonb
    private Prices prices;
    private String purchaseUris;
    private String relatedUris;
    private String preview;

    /**
     * Helper function to map a jOOQ Record to this POJO.
     */
    public static ScryfallCardObject mapFromRecord(Record record) {
        return ScryfallCardObject.builder()
                .id(record.get(CARDS.ID))
                .name(record.get(CARDS.NAME))
                .typeLine(record.get(CARDS.TYPE_LINE))
                .toughness(record.get(CARDS.TOUGHNESS))
                .power(record.get(CARDS.POWER))
                .artist(record.get(CARDS.ARTIST))
                .cmc(record.get(CARDS.CMC)) // Handled by custom builder
                .scryfallUri(record.get(CARDS.SCRYFALL_URI))
                .colorIdentity(record.get(CARDS.COLOR_IDENTITY, String[].class)) // Handled by custom builder
                .multiverseIds(record.get(CARDS.MULTIVERSE_IDS, Integer[].class))
                .build();
    }

    public static class ScryfallCardObjectBuilder {

        // Helper to convert String[] from jOOQ to List<String> for the model
        public ScryfallCardObjectBuilder colorIdentity(String[] colors) {
            this.colorIdentity = Optional.ofNullable(colors)
                    .map(List::of)
                    .orElseGet(List::of);
            return this;
        }

        // Helper to convert BigDecimal from jOOQ to Integer for the model
        public ScryfallCardObjectBuilder cmc(BigDecimal cmc) {
            this.cmc = Optional.ofNullable(cmc)
                    .map(BigDecimal::intValue)
                    .orElse(0);
            return this;
        }
    }
}