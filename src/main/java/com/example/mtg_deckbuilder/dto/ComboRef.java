package com.example.mtg_deckbuilder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComboRef {
    public int id;
}
