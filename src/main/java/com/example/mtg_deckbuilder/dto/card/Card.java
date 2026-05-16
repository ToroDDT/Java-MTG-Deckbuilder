package com.example.mtg_deckbuilder.dto.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.sql.Array;
import java.sql.ResultSet;
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
                .id(rs.getObject("card_id", UUID.class))
                .name(rs.getString("name"))
                .typeLine(rs.getString("type_line"))
                .toughness(rs.getString("toughness"))
                .power(rs.getString("power"))
                .artist(rs.getString("artist"))
                .cmc(rs.getInt("cmc"))
                .scryfallUri(rs.getString("scryfall_uri"))

                // already extracted in SQL
                .image(rs.getString("image"))

                .colorIdentity(extractColorIdentity(rs))

                .prices(
                        Prices.builder()
                                .usd(parseDouble(rs.getString("usd")))
                                .usdFoil(parseDouble(rs.getString("usd_foil")))
                                .eurFoil(parseDouble(rs.getString("eur_foil")))
                                .tix(parseDouble(rs.getString("tix")))
                                .build()
                )

                .build();
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
}