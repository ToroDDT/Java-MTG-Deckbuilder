package com.example.mtg_deckbuilder.views;

import com.example.mtg_deckbuilder.model.OwnedCard;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record BuilderViewModel(
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
