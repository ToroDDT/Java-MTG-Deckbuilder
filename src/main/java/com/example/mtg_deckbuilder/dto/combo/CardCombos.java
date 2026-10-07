package com.example.mtg_deckbuilder.dto.combo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class CardCombos {
    private String location; // Common location context if applicable
    @Builder.Default
    private List<ComboItem> items = List.of();
}
