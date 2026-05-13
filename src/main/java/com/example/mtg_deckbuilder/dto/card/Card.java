package com.example.mtg_deckbuilder.dto.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.ObjectMapper;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;



@Setter
@Getter
@Builder
@AllArgsConstructor
public class Card {
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


    private final ObjectMapper objectMapper = new ObjectMapper();


    public Card extractFields(ResultSet rs) throws SQLException {
        return Card.builder()
                .id(rs.getObject("card_id", UUID.class))
                .name(rs.getString("name"))
                .typeLine(rs.getString("type_line"))
                .toughness(rs.getString("toughness"))
                .power(rs.getString("artist"))
                .cmc(rs.getInt("cmc"))
                .scryfallUri(rs.getString("scryfall_uri"))
                .colors(extractColorIdentity(rs))
                .colorIdentity(extractColorIdentity(rs))
                .image(extractArtCrop(rs))
                .prices(extractCardPrices(rs))
                .build();
    }
    // Helper method to check column existence
    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            if (columnName.equalsIgnoreCase(rsmd.getColumnName(x))) {
                return true;
            }
        }
        return false;
    }

    private String extractArtCrop(ResultSet rs) throws SQLException {
        String raw = rs.getString("image_uris");
        if (raw == null || raw.isBlank()) return null;
        try {
            return objectMapper.readValue(raw, ImageUris.class).getBorderCrop();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Prices extractCardPrices(ResultSet rs) throws SQLException {
        String raw = rs.getString("prices");

        if (raw == null || raw.isBlank()) {
            return Prices.builder()
                    .tix(0.0).eurFoil(0.0).usdFoil(0.0).usd(0.0)
                    .build();
        }

        try {
            Prices prices = objectMapper.readValue(raw, Prices.class);

            if (prices.getUsd() == null) {
                // If both are null, it still remains null, which is fine
                // because we handle that in the UI or set to 0.0 below
                prices.setUsd(prices.getUsdFoil() != null ? prices.getUsdFoil() : 0.0);
            }

            if (prices.getUsdFoil() == null) prices.setUsdFoil(0.0);
            if (prices.getEurFoil() == null) prices.setEurFoil(0.0);
            if (prices.getTix() == null) prices.setTix(0.0);
            return prices; // Essential: Return the processed object

        } catch (Exception e) {
            e.printStackTrace();
            // Return a zeroed-out builder on parse error rather than null
            return Prices.builder().tix(0.0).eurFoil(0.0).usdFoil(0.0).usd(0.0).build();
        }
    }

    private Integer[] extractMultiverseIds(ResultSet rs) throws SQLException {
        Array array = rs.getArray("multiverse_ids");
        if (array == null) return new Integer[0];
        try {
            return (Integer[]) array.getArray();
        } finally {
            array.free();
        }
    }

    private List<String> extractColorIdentity(ResultSet rs) throws SQLException {
        Array array = rs.getArray("color_identity");
        if (array == null) return new ArrayList<>();

        try {
            String cleanString = array.toString().replaceAll("[^a-zA-Z]", "");
            return Arrays.stream(cleanString.chars()
                            .mapToObj(c -> String.valueOf((char) c))
                            .toArray(String[]::new))
                    .toList();
        } finally {
            array.free();
        }

    }

}