package com.example.mtg_deckbuilder.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Array;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Objects;



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
    private BigDecimal cmc;
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
    private String prices;
    private String purchaseUris;
    private String relatedUris;
    private String preview;

    // Default Constructor
    public Card() {}

    public UUID getId() {
        return id;
    }

    public String getOracleId() {
        return oracleId;
    }

    public Integer[] getMultiverseIds() {
        return multiverseIds;
    }

    public Integer getMtgoId() {
        return mtgoId;
    }

    public Integer getMtgoFoilId() {
        return mtgoFoilId;
    }

    public Integer getTcgplayerId() {
        return tcgplayerId;
    }

    public Integer getTcgplayerEtchedId() {
        return tcgplayerEtchedId;
    }

    public Integer getCardmarketId() {
        return cardmarketId;
    }

    public String getLang() {
        return lang;
    }

    public LocalDate getReleasedAt() {
        return releasedAt;
    }

    public String getUri() {
        return uri;
    }

    public String getScryfallUri() {
        return scryfallUri;
    }

    public String getLayout() {
        return layout;
    }

    public Boolean getHighresImage() {
        return highresImage;
    }

    public String getImageStatus() {
        return imageStatus;
    }

    public String getImageUris() {
        return imageUris;
    }

    public String getName() {
        return name;
    }

    public String getManaCost() {
        return manaCost;
    }

    public BigDecimal getCmc() {
        return cmc;
    }

    public String getTypeLine() {
        return typeLine;
    }

    public String getOracleText() {
        return oracleText;
    }

    public String getPower() {
        return power;
    }

    public String getToughness() {
        return toughness;
    }

    public String getLoyalty() {
        return loyalty;
    }

    public String getDefense() {
        return defense;
    }

    public List<String> getColors() {
        return colors;
    }

    public List<String> getColorIdentity() {
        return colorIdentity;
    }

    public List<String> getColorIndicator() {
        return colorIndicator;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public List<String> getProducedMana() {
        return producedMana;
    }

    public Boolean getReserved() {
        return reserved;
    }

    public Boolean getGameChanger() {
        return gameChanger;
    }

    public String getLegalities() {
        return legalities;
    }

    public String getAllParts() {
        return allParts;
    }

    public String getCardFaces() {
        return cardFaces;
    }

    public String getArtist() {
        return artist;
    }

    public List<String> getArtistIds() {
        return artistIds;
    }

    public String getIllustrationId() {
        return illustrationId;
    }

    public String getFlavorText() {
        return flavorText;
    }

    public String getFlavorName() {
        return flavorName;
    }

    public String getWatermark() {
        return watermark;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public String getFrame() {
        return frame;
    }

    public List<String> getFrameEffects() {
        return frameEffects;
    }

    public String getSecurityStamp() {
        return securityStamp;
    }

    public Boolean getFullArt() {
        return fullArt;
    }

    public Boolean getTextless() {
        return textless;
    }

    public Boolean getOversized() {
        return oversized;
    }

    public Boolean getBooster() {
        return booster;
    }

    public Boolean getDigital() {
        return digital;
    }

    public Boolean getFoil() {
        return foil;
    }

    public Boolean getNonfoil() {
        return nonfoil;
    }

    public List<String> getFinishes() {
        return finishes;
    }

    public List<String> getGames() {
        return games;
    }

    public Boolean getPromo() {
        return promo;
    }

    public List<String> getPromoTypes() {
        return promoTypes;
    }

    public Boolean getReprint() {
        return reprint;
    }

    public Boolean getVariation() {
        return variation;
    }

    public String getVariationOf() {
        return variationOf;
    }

    public Boolean getStorySpotlight() {
        return storySpotlight;
    }

    public String getCollectorNumber() {
        return collectorNumber;
    }

    public String getRarity() {
        return rarity;
    }

    public String getCardBackId() {
        return cardBackId;
    }

    public String getSetId() {
        return setId;
    }

    public String getSetCode() {
        return setCode;
    }

    public String getSetName() {
        return setName;
    }

    public String getSetType() {
        return setType;
    }

    public String getSetUri() {
        return setUri;
    }

    public String getSetSearchUri() {
        return setSearchUri;
    }

    public String getScryfallSetUri() {
        return scryfallSetUri;
    }

    public String getRulingsUri() {
        return rulingsUri;
    }

    public String getPrintsSearchUri() {
        return printsSearchUri;
    }

    public Integer getEdhrecRank() {
        return edhrecRank;
    }

    public Integer getPennyRank() {
        return pennyRank;
    }

    public String getHandModifier() {
        return handModifier;
    }

    public String getLifeModifier() {
        return lifeModifier;
    }

    public String getPrices() {
        return prices;
    }

    public String getPurchaseUris() {
        return purchaseUris;
    }

    public String getRelatedUris() {
        return relatedUris;
    }

    public String getPreview() {
        return preview;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setOracleId(String oracleId) {
        this.oracleId = oracleId;
    }

    public void setMultiverseIds(Integer[] multiverseIds) {
        this.multiverseIds = multiverseIds;
    }

    public void setMtgoId(Integer mtgoId) {
        this.mtgoId = mtgoId;
    }

    public void setMtgoFoilId(Integer mtgoFoilId) {
        this.mtgoFoilId = mtgoFoilId;
    }

    public void setTcgplayerId(Integer tcgplayerId) {
        this.tcgplayerId = tcgplayerId;
    }

    public void setTcgplayerEtchedId(Integer tcgplayerEtchedId) {
        this.tcgplayerEtchedId = tcgplayerEtchedId;
    }

    public void setCardmarketId(Integer cardmarketId) {
        this.cardmarketId = cardmarketId;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setReleasedAt(LocalDate releasedAt) {
        this.releasedAt = releasedAt;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setScryfallUri(String scryfallUri) {
        this.scryfallUri = scryfallUri;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public void setHighresImage(Boolean highresImage) {
        this.highresImage = highresImage;
    }

    public void setImageStatus(String imageStatus) {
        this.imageStatus = imageStatus;
    }

    public void setImageUris(String imageUris) {
        this.imageUris = imageUris;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setManaCost(String manaCost) {
        this.manaCost = manaCost;
    }

    public void setCmc(BigDecimal cmc) {
        this.cmc = cmc;
    }

    public void setTypeLine(String typeLine) {
        this.typeLine = typeLine;
    }

    public void setOracleText(String oracleText) {
        this.oracleText = oracleText;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public void setToughness(String toughness) {
        this.toughness = toughness;
    }

    public void setLoyalty(String loyalty) {
        this.loyalty = loyalty;
    }

    public void setDefense(String defense) {
        this.defense = defense;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public void setColorIdentity(List<String> colorIdentity) {
        this.colorIdentity = colorIdentity;
    }

    public void setColorIndicator(List<String> colorIndicator) {
        this.colorIndicator = colorIndicator;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public void setProducedMana(List<String> producedMana) {
        this.producedMana = producedMana;
    }

    public void setReserved(Boolean reserved) {
        this.reserved = reserved;
    }

    public void setGameChanger(Boolean gameChanger) {
        this.gameChanger = gameChanger;
    }

    public void setLegalities(String legalities) {
        this.legalities = legalities;
    }

    public void setAllParts(String allParts) {
        this.allParts = allParts;
    }

    public void setCardFaces(String cardFaces) {
        this.cardFaces = cardFaces;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setArtistIds(List<String> artistIds) {
        this.artistIds = artistIds;
    }

    public void setIllustrationId(String illustrationId) {
        this.illustrationId = illustrationId;
    }

    public void setFlavorText(String flavorText) {
        this.flavorText = flavorText;
    }

    public void setFlavorName(String flavorName) {
        this.flavorName = flavorName;
    }

    public void setWatermark(String watermark) {
        this.watermark = watermark;
    }

    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public void setFrameEffects(List<String> frameEffects) {
        this.frameEffects = frameEffects;
    }

    public void setSecurityStamp(String securityStamp) {
        this.securityStamp = securityStamp;
    }

    public void setFullArt(Boolean fullArt) {
        this.fullArt = fullArt;
    }

    public void setTextless(Boolean textless) {
        this.textless = textless;
    }

    public void setOversized(Boolean oversized) {
        this.oversized = oversized;
    }

    public void setBooster(Boolean booster) {
        this.booster = booster;
    }

    public void setDigital(Boolean digital) {
        this.digital = digital;
    }

    public void setFoil(Boolean foil) {
        this.foil = foil;
    }

    public void setNonfoil(Boolean nonfoil) {
        this.nonfoil = nonfoil;
    }

    public void setFinishes(List<String> finishes) {
        this.finishes = finishes;
    }

    public void setGames(List<String> games) {
        this.games = games;
    }

    public void setPromo(Boolean promo) {
        this.promo = promo;
    }

    public void setPromoTypes(List<String> promoTypes) {
        this.promoTypes = promoTypes;
    }

    public void setReprint(Boolean reprint) {
        this.reprint = reprint;
    }

    public void setVariation(Boolean variation) {
        this.variation = variation;
    }

    public void setVariationOf(String variationOf) {
        this.variationOf = variationOf;
    }

    public void setStorySpotlight(Boolean storySpotlight) {
        this.storySpotlight = storySpotlight;
    }

    public void setCollectorNumber(String collectorNumber) {
        this.collectorNumber = collectorNumber;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public void setCardBackId(String cardBackId) {
        this.cardBackId = cardBackId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public void setSetCode(String setCode) {
        this.setCode = setCode;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }

    public void setSetType(String setType) {
        this.setType = setType;
    }

    public void setSetUri(String setUri) {
        this.setUri = setUri;
    }

    public void setSetSearchUri(String setSearchUri) {
        this.setSearchUri = setSearchUri;
    }

    public void setScryfallSetUri(String scryfallSetUri) {
        this.scryfallSetUri = scryfallSetUri;
    }

    public void setRulingsUri(String rulingsUri) {
        this.rulingsUri = rulingsUri;
    }

    public void setPrintsSearchUri(String printsSearchUri) {
        this.printsSearchUri = printsSearchUri;
    }

    public void setEdhrecRank(Integer edhrecRank) {
        this.edhrecRank = edhrecRank;
    }

    public void setPennyRank(Integer pennyRank) {
        this.pennyRank = pennyRank;
    }

    public void setHandModifier(String handModifier) {
        this.handModifier = handModifier;
    }

    public void setLifeModifier(String lifeModifier) {
        this.lifeModifier = lifeModifier;
    }

    public void setPrices(String prices) {
        this.prices = prices;
    }

    public void setPurchaseUris(String purchaseUris) {
        this.purchaseUris = purchaseUris;
    }

    public void setRelatedUris(String relatedUris) {
        this.relatedUris = relatedUris;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}