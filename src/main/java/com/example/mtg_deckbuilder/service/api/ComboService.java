package com.example.mtg_deckbuilder.service.api;

import com.example.mtg_deckbuilder.dto.combo.CardCombos;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;

public interface ComboService {
    CardCombos findCombos(CustomUserDetails userId, LibraryFilters libraryFilters) throws Exception;
    CardCombos findCombos(CustomUserDetails userId) throws Exception;
    void saveCombos(CustomUserDetails user, CardCombos cardCombos) throws JsonProcessingException;
    CardCombos getCombos(CustomUserDetails user);
}
