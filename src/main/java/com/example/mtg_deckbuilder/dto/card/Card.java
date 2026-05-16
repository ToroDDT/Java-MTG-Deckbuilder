package com.example.mtg_deckbuilder.dto.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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

    private String layout;
    private Boolean highresImage;
    private String imageStatus;
    private String imageUris;

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

    private Prices prices;

    private String artist;

    public static Card fromResultSet(ResultSet rs) throws SQLException {

        return Card.builder()
                .id(extractId(rs))
                .name(rs.getString("name"))
                .typeLine(rs.getString("type_line"))
                .toughness(rs.getString("toughness"))
                .power(rs.getString("power"))
                .artist(rs.getString("artist"))
                .cmc(rs.getInt("cmc"))
                .scryfallUri(rs.getString("scryfall_uri"))

                .image(extractImage(rs))

                .colorIdentity(extractColorIdentity(rs))

                .prices(extractPrices(rs))

                .build();
    }

    private static UUID extractId(ResultSet rs) throws SQLException {
        String idColumn = hasColumn(rs, "card_id") ? "card_id" : "id";
        return rs.getObject(idColumn, UUID.class);
    }

    private static String extractImage(ResultSet rs) throws SQLException {
        if (hasColumn(rs, "image")) {
            return rs.getString("image");
        }

        String raw = rs.getString("image_uris");
        if (raw == null || raw.isBlank()) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(raw, ImageUris.class).getBorderCrop();
        } catch (Exception e) {
            return null;
        }
    }

    private static Prices extractPrices(ResultSet rs) throws SQLException {
        if (hasColumn(rs, "usd")) {
            return Prices.builder()
                    .usd(parseDouble(rs.getString("usd")))
                    .usdFoil(parseDouble(rs.getString("usd_foil")))
                    .eurFoil(parseDouble(rs.getString("eur_foil")))
                    .tix(parseDouble(rs.getString("tix")))
                    .build();
        }

        String raw = rs.getString("prices");
        if (raw == null || raw.isBlank()) {
            return zeroPrices();
        }

        try {
            Prices prices = OBJECT_MAPPER.readValue(raw, Prices.class);
            if (prices.getUsd() == null) prices.setUsd(0.0);
            if (prices.getUsdFoil() == null) prices.setUsdFoil(0.0);
            if (prices.getEurFoil() == null) prices.setEurFoil(0.0);
            if (prices.getTix() == null) prices.setTix(0.0);
            return prices;
        } catch (Exception e) {
            return zeroPrices();
        }
    }

    private static Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return 0.0;
        }

        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private static List<String> extractColorIdentity(ResultSet rs)
            throws SQLException {

        Array array = rs.getArray("color_identity");

        if (array == null) {
            return List.of();
        }

        try {
            String[] values = (String[]) array.getArray();
            return Arrays.asList(values);
        } finally {
            array.free();
        }
    }

    private static boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int index = 1; index <= columnCount; index++) {
            if (columnName.equalsIgnoreCase(metaData.getColumnLabel(index))
                    || columnName.equalsIgnoreCase(metaData.getColumnName(index))) {
                return true;
            }
        }

        return false;
    }

    private static Prices zeroPrices() {
        return Prices.builder()
                .usd(0.0)
                .usdFoil(0.0)
                .eurFoil(0.0)
                .tix(0.0)
                .build();
    }
}
