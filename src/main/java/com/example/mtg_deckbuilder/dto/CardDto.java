package com.example.mtg_deckbuilder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class CardDto {
    public int id;
    public String name;
    public boolean spoiler;
    public String oracleId;
    public String typeLine;
    public String imageUriFrontPng;
    public String imageUriFrontLarge;
    public String imageUriFrontSmall;
    public String imageUriFrontNormal;
    public String imageUriFrontArtCrop;
    public String imageUriBackPng;
    public String imageUriBackLarge;
    public String imageUriBackSmall;
    public String imageUriBackNormal;
    public String imageUriBackArtCrop;
    public String layoutRotationFront;
}
