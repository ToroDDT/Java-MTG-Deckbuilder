package com.example.mtg_deckbuilder.service.api;

import com.example.mtg_deckbuilder.dto.combo.CardCombos;
import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.example.mtg_deckbuilder.views.api.ComboDetailViewModel;

import java.util.List;
import java.util.Optional;


public interface ComboService {
    void updateCombos(CustomUserDetails user) throws Exception;
    void saveCombos(CustomUserDetails user, CardCombos cardCombos) throws JsonProcessingException;
    CardCombos getCombos(CustomUserDetails user);
    CardCombos getCombos(CustomUserDetails user, LibraryFilters filters);
    List<String> getLocations(CustomUserDetails user);
    Optional<ComboDetailViewModel> getComboDetail(CustomUserDetails user, String location, String cardsKey, String description) throws Exception;
}
