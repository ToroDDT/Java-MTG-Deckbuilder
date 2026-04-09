package com.example.mtg_deckbuilder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TemplateDto {
    public int id;
    public String name;
    public String scryfallApi;
    public String scryfallQuery;
}
