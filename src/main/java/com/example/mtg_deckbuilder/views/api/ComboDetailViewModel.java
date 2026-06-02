package com.example.mtg_deckbuilder.views.api;

import java.util.List;

public interface ComboDetailViewModel {

    String title();

    String location();

    String identity();

    List<String> identityColors();

    boolean spoiler();

    List<String> cardNames();

    List<String> cardImageUrls();

    List<String> heroImageUrls();

    List<String> initialStateLines();

    String notablePrerequisites();

    String manaNeeded();

    List<String> stepLines();

    String notes();

    List<String> resultLines();

    String tcgplayerPrice();

    String cardkingdomPrice();

    List<? extends LegalityRow> legalities();

    interface LegalityRow {

        String format();

        boolean legal();
    }
}
