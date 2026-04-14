package com.example.mtg_deckbuilder.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor  // Necessary: Jackson uses this to create the instance
@AllArgsConstructor // Necessary: Allows @Builder to coexist with NoArgs
public class Prices {

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