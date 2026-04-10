package com.example.mtg_deckbuilder.dto;

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

}
