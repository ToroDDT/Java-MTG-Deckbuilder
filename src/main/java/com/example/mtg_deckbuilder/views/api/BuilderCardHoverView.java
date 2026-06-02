package com.example.mtg_deckbuilder.views.api;

import java.util.List;

public interface BuilderCardHoverView {

    String cardName();

    String imageUrl();

    String price();

    List<String> tags();
}
