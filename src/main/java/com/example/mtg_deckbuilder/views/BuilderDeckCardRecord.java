package com.example.mtg_deckbuilder.views;

import lombok.Builder;

import java.util.UUID;

/**
 * One row of deck builder card data (deck list / mana curve / totals), mapped from
 * {@code getAllCardsForUser} SQL — not the full Scryfall {@link com.example.mtg_deckbuilder.dto.card.Card} entity.
 */
@Builder
public record BuilderDeckCardRecord(
        String deckEntryId,
        String name,
        String colorIdentity,
        String typeLine,
        String producedMana,
        String cmc,
        String commander,
        /** Deck-level image URL ({@code decks.image}), same for each row in a deck query. */
        String deckImage,
        /** Plain USD amount for {@link Double#parseDouble}, e.g. {@code "12.34"}. */
        String priceUsd,
        String deckName
) {
}
