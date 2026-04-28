package com.example.mtg_deckbuilder.views;

import com.example.mtg_deckbuilder.model.OwnedCard;

import java.util.List;

public record BuilderViewModel(
        List<OwnedCard> creatures,
        List<OwnedCard> spells,
        List<OwnedCard> sorceries
) {
}
