package com.example.mtg_deckbuilder.repository.api;

import com.example.mtg_deckbuilder.dto.combo.CardCombos;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public interface ComboRepository{
    void saveCombos(CustomUserDetails owner, CardCombos combos) throws JsonProcessingException;
    CardCombos getCombos (CustomUserDetails owner);
    List<String> getLocations(CustomUserDetails owner);

    /** Moves saved combo data when a deck is renamed (locations are stored by deck name). */
    int renameLocation(CustomUserDetails owner, String oldLocation, String newLocation);
}
