package com.example.mtg_deckbuilder.dto.combo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureProduced {
    public FeatureDto feature;
    public int quantity;
}
