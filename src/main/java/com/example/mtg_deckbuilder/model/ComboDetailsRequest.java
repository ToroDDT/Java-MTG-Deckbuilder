package com.example.mtg_deckbuilder.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
public class ComboDetailsRequest {

    @Setter
    private String location;
    private String cards;
    @Setter
    private String description;

    // Derived field populated during binding
    private List<String> selectedCardNames = Collections.emptyList();

    /**
     * Custom setter invoked automatically by Spring MVC during Form/Query parameter binding.
     */
    public void setCards(String cards) {
        this.cards = cards;
        this.selectedCardNames = parseCards(cards);
    }

    private List<String> parseCards(String rawCards) {
        if (rawCards == null || rawCards.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(rawCards.split("\\|\\|"))
                     .map(String::trim)
                     .filter(s -> !s.isEmpty())
                     .toList();
    }
}