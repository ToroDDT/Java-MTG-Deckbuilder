package com.example.mtg_deckbuilder.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Combos {
    public Integer count;
    public String next;
    public String previous;
    public ComboResults results;
}