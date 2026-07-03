package com.example.mtg_deckbuilder.service.api;

import com.example.mtg_deckbuilder.model.LibraryFilters;
import com.example.mtg_deckbuilder.views.api.CardCatalogViewModel;

public interface CardBrowserService {

    CardCatalogViewModel buildViewModel();

    CardCatalogViewModel buildViewModel(LibraryFilters filters);
}
