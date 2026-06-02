package com.example.mtg_deckbuilder.views.impl;

import com.example.mtg_deckbuilder.views.api.BuilderCardHoverView;

import java.util.List;

public record BuilderCardHoverViewImpl(String cardName, String imageUrl, String price, List<String> tags)
        implements BuilderCardHoverView {
}
