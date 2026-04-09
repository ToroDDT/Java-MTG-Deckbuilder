package com.example.mtg_deckbuilder.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComboVariant {
    public String id;
    public String identity;
    public String status;
    public boolean spoiler;
    public String description;
    public String manaNeeded;
    public int manaValueNeeded;
    public int popularity;
    public String bracketTag;
    public String notes;
    public String easyPrerequisites;
    public String notablePrerequisites;
    public int variantCount;
    public Prices prices;
    public Legalities legalities;
    public List<CardUse> uses;
    public List<TemplateRequirement> requires;
    public List<FeatureProduced> produces;
    public List<ComboRef> includes;
    public List<ComboRef> of;
}
