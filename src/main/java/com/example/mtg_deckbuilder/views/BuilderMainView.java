package com.example.mtg_deckbuilder.views;

import com.example.mtg_deckbuilder.security.CustomUserDetails;
import lombok.Builder;

import java.util.List;

@Builder
public record BuilderMainView(
        BuilderViewModel builderView,
        List<String> manaCurveLabels,
        List<Long> manaCurveData,
        List<Long> colorProductionData,
        long colorProductionTotal,
        List<String> colorProductionLabels,
        CustomUserDetails user
) {
}
