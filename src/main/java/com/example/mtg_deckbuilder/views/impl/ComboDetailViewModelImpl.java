package com.example.mtg_deckbuilder.views.impl;

import com.example.mtg_deckbuilder.views.api.ComboDetailViewModel;

import java.util.List;

public record ComboDetailViewModelImpl(
        String title,
        String location,
        String identity,
        List<String> identityColors,
        boolean spoiler,
        List<String> cardNames,
        List<String> cardImageUrls,
        List<String> heroImageUrls,
        List<String> initialStateLines,
        String notablePrerequisites,
        String manaNeeded,
        List<String> stepLines,
        String notes,
        List<String> resultLines,
        String tcgplayerPrice,
        String cardkingdomPrice,
        List<LegalityRowImpl> legalities
) implements ComboDetailViewModel {

    public record LegalityRowImpl(String format, boolean legal) implements LegalityRow {
    }
}
