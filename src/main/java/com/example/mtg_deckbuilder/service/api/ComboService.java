package com.example.mtg_deckbuilder.service.api;

import com.example.mtg_deckbuilder.dto.CardCombos;
import com.example.mtg_deckbuilder.security.CustomUserDetails;

public interface ComboService {
    CardCombos findCombos(CustomUserDetails userId) throws Exception;
}
