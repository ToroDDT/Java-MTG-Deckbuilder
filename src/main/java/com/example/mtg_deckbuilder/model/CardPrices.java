package com.example.mtg_deckbuilder.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
    public CardPrices() {}

    public CardPrices(Double usd, Double usdFoil, Double usdEtched,
                     Double eur, Double eurFoil, Double tix) {
        this.usd = usd;
        this.usdFoil = usdFoil;
        this.usdEtched = usdEtched;
        this.eur = eur;
        this.eurFoil = eurFoil;
        this.tix = tix;
    }
}
