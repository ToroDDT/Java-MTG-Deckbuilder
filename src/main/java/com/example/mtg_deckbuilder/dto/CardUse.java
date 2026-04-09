package com.example.mtg_deckbuilder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CardUse {
    public CardDto card;
    public int quantity;
    public List<String> zoneLocations;
    public boolean mustBeCommander;
    public String exileCardState;
    public String libraryCardState;
    public String graveyardCardState;
    public String battlefieldCardState;
}
