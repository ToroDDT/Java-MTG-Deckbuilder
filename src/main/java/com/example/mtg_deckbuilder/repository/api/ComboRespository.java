package com.example.mtg_deckbuilder.repository.api;

import com.example.mtg_deckbuilder.dto.CardCombos;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ComboRespository {
    void saveCombos(CustomUserDetails owner, CardCombos combos) throws JsonProcessingException;
    CardCombos getCombos (CustomUserDetails owner);
}
