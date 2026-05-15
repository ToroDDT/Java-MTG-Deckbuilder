package com.example.mtg_deckbuilder.views;

import java.util.List;

public record ComboDetailViewModel(
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
        List<LegalityRow> legalities
) {
    public record LegalityRow(String format, boolean legal) {
    }
}
