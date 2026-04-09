package com.example.mtg_deckbuilder.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeatureDto {
    public int id;
    public String name;
    public String status;
    public boolean uncountable;
}
