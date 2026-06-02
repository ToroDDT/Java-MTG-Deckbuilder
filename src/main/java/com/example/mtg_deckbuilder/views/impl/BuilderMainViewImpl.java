package com.example.mtg_deckbuilder.views.impl;

import com.example.mtg_deckbuilder.security.CustomUserDetails;
import com.example.mtg_deckbuilder.views.api.BuilderMainView;
import com.example.mtg_deckbuilder.views.api.BuilderViewModel;
import lombok.Builder;

import java.util.List;

@Builder
public record BuilderMainViewImpl(
        BuilderViewModel builderView,
        List<String> manaCurveLabels,
        List<Long> manaCurveData,
        List<Long> colorProductionData,
        long colorProductionTotal,
        List<String> colorProductionLabels,
        CustomUserDetails user
) implements BuilderMainView {

    private static final List<String> MANA_CURVE_LABELS =
            List.of("0", "1", "2", "3", "4", "5", "6", "7+");
    private static final List<String> COLOR_PRODUCTION_LABELS =
            List.of("Red", "White", "Green", "Black", "Blue", "Colorless");

    public static BuilderMainView from(BuilderViewModel view, CustomUserDetails user) {
        return BuilderMainViewImpl.builder()
                .builderView(view)
                .manaCurveLabels(MANA_CURVE_LABELS)
                .manaCurveData(view.manaCurveData())
                .colorProductionData(view.colorProduction())
                .colorProductionTotal(view.colorProduction()
                        .stream()
                        .mapToLong(Long::longValue)
                        .sum())
                .colorProductionLabels(COLOR_PRODUCTION_LABELS)
                .user(user)
                .build();
    }
}
