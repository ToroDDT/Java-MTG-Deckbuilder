package com.example.mtg_deckbuilder.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComboResults {
    public String identity;
    public List<ComboVariant> included;
    public List<ComboVariant> includedByChangingCommanders;
    public List<ComboVariant> almostIncluded;
    public List<ComboVariant> almostIncludedByAddingColors;
    public List<ComboVariant> almostIncludedByChangingCommanders;
    public List<ComboVariant> almostIncludedByAddingColorsAndChangingCommanders;
}