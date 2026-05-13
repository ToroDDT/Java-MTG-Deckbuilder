package com.example.mtg_deckbuilder.repository.api;

import com.example.mtg_deckbuilder.dto.combo.CardCombos;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ComboRepository{
    void saveCombos(CustomUserDetails owner, CardCombos combos) throws JsonProcessingException;
    CardCombos getCombos (CustomUserDetails owner);
}