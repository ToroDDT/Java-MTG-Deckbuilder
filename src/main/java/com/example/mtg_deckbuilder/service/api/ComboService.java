package com.example.mtg_deckbuilder.service.api;

import com.example.mtg_deckbuilder.dto.combo.CardCombos;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;


public interface ComboService {
    void updateCombos(CustomUserDetails user) throws Exception;
    void saveCombos(CustomUserDetails user, CardCombos cardCombos) throws JsonProcessingException;
    CardCombos getCombos(CustomUserDetails user);
}