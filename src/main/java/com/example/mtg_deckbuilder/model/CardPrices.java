package com.example.mtg_deckbuilder.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CardPrices {
    @JsonProperty("usd")
    private Double usd;

    @JsonProperty("usd_foil")
    private Double usdFoil;

    @JsonProperty("usd_etched")
    private Double usdEtched;

    @JsonProperty("eur")
    private Double eur;

    @JsonProperty("eur_foil")
    private Double eurFoil;

    @JsonProperty("tix")
    private Double tix;
}
