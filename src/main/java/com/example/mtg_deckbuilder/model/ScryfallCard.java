package com.example.mtg_deckbuilder.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScryfallCard {

    public String name;

    @JsonProperty("image_uris")
    public ImageUris imageUris;

    public Prices prices;

    // Static inner classes for the nested data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImageUris {
        public String normal;
        public String png;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Prices {
        public String usd;
        @JsonProperty("usd_foil")
        public String usdFoil;
    }
}