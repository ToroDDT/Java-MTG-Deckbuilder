package com.example.mtg_deckbuilder.dto.combo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
@Builder
@Getter
@AllArgsConstructor
public class ComboItem {
    private List<String> cardCombination;
    private String description;
    List<String> images;
    private String location;
    private String result;
    private ComboVariant variant;
}
