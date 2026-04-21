package com.example.mtg_deckbuilder.views;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class DeckViewModelImpl {
    List<String> legalCommanders;
}
