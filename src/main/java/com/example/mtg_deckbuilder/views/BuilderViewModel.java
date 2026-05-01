package com.example.mtg_deckbuilder.views;


import java.util.List;
import java.util.Map;

public record BuilderViewModel(
        String image,
        Double totalValue,
        String deckName,
        List<Map<String, String>>creatures,
        List<Long> manaCurveData,
        List<Map<String, String>>instants,
        List<Map<String, String>> enchantments,
        List<Map<String, String>> artifacts,
        List<Map<String, String>> lands,
        List<Map<String, String>> sorceries,
        String deckId
) {
}
