package com.example.mtg_deckbuilder.views;

import java.util.List;

public record BuilderCardHoverView(String cardName, String imageUrl, String price, List<String> tags) {
}
