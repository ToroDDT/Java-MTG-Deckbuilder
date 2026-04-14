package com.example.mtg_deckbuilder.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;



@Setter
@Getter
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

    // Default Constructor
    public Card() {}

}