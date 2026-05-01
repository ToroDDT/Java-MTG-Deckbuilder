package com.example.mtg_deckbuilder.service.api;

import com.example.mtg_deckbuilder.views.BuilderViewModel;

import java.util.UUID;

public interface BuilderService {
    BuilderViewModel getBuilderView(String deckId);
    String optimizeDecksAgainstOpponent();
    String optimizeDeck();
}
