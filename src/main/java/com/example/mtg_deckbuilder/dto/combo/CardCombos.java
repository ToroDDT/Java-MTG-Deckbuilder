package com.example.mtg_deckbuilder.dto.combo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class CardCombos {
    private List<List<String>> cardCombinations;
    private List<String> description;
    private List<List<String>> images;
    private List<String> locations;
    private String location;
    private List<String> results;
    /** Full Commander Spellbook variant payloads, parallel to {@link #cardCombinations}. */
    private List<ComboVariant> variants;
}
