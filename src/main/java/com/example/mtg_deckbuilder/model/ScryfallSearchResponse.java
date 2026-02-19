package com.example.mtg_deckbuilder.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScryfallSearchResponse {

    public List<ScryfallCard> data;

    @JsonProperty("total_cards")
    public int totalCards;

    @JsonProperty("has_more")
    public boolean hasMore;
}