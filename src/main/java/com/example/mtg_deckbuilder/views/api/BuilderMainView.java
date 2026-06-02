package com.example.mtg_deckbuilder.views.api;

import com.example.mtg_deckbuilder.security.CustomUserDetails;

import java.util.List;

public interface BuilderMainView {

    BuilderViewModel builderView();

    List<String> manaCurveLabels();

    List<Long> manaCurveData();

    List<Long> colorProductionData();

    long colorProductionTotal();

    List<String> colorProductionLabels();

    CustomUserDetails user();
}
