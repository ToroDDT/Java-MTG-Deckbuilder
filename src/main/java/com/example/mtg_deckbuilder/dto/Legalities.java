package com.example.mtg_deckbuilder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Legalities {
    public boolean brawl;
    public boolean predh;
    public boolean legacy;
    public boolean modern;
    public boolean pauper;
    public boolean pioneer;
    public boolean vintage;
    public boolean standard;
    public boolean commander;
    public boolean premodern;
    public boolean oathbreaker;
    public boolean pauperCommander;
    public boolean pauperCommanderMain;
}
