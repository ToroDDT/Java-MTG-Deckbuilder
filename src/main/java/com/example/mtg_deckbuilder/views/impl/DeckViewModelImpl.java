package com.example.mtg_deckbuilder.views.impl;

import com.example.mtg_deckbuilder.views.api.DeckViewModel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class DeckViewModelImpl implements DeckViewModel {
    List<String> legalCommanders;
}
