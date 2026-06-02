package com.example.mtg_deckbuilder.views.impl;

import com.example.mtg_deckbuilder.views.api.CardTagsViewModel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CardTagsViewModelImpl implements CardTagsViewModel {
    private String id;
    private List<String> tags;
}
