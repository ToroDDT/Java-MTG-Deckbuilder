package com.example.mtg_deckbuilder.views;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CardTagsViewModel {
    private String id;
    private List<String> tags;
}
