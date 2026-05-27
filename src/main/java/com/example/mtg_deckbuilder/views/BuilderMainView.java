package com.example.mtg_deckbuilder.views;

import java.util.List;

public record BuilderMainView(
        BuilderViewModel builderView,
        List<String> manaCurveLabels,
        List<Long> manaCurveData,
        List<Long> colorProductionData,
        long colorProductionTotal,
        List<String> colorProductionLabels
) {
}
