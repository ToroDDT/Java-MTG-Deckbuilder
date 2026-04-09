package com.example.mtg_deckbuilder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemplateRequirement {
    public int quantity;
    public TemplateDto template;
    public List<String> zoneLocations;
    public boolean mustBeCommander;
    public String exileCardState;
    public String libraryCardState;
    public String graveyardCardState;
    public String battlefieldCardState;
}
