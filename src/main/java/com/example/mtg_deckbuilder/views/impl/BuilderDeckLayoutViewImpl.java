package com.example.mtg_deckbuilder.views.impl;

import com.example.mtg_deckbuilder.views.api.BuilderDeckLayoutView;
import com.example.mtg_deckbuilder.views.api.BuilderDeckSection;
import com.example.mtg_deckbuilder.views.api.BuilderViewModel;
import com.example.mtg_deckbuilder.views.api.DeckLayoutExtrasFlags;
import lombok.Builder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
public record BuilderDeckLayoutViewImpl(
        BuilderViewModel builderView,
        String deckViewStyle,
        DeckLayoutExtrasFlags deckExtras,
        List<BuilderDeckSection> deckSections,
        boolean deckListCondensed,
        boolean deckVisualSplit,
        boolean deckSpoilerReveal
) implements BuilderDeckLayoutView {

    private static final Set<String> ALLOWED_VIEW_STYLES = Set.of(
            "text",
            "condensed",
            "visual-grid",
            "visual-stacks",
            "visual-split",
            "visual-spoiler"
    );

    private static final Set<String> ALLOWED_GROUP_BY = Set.of(
            "type",
            "subtype",
            "type-tag",
            "rarity",
            "color",
            "color-identity",
            "mana-value",
            "set",
            "artist",
            "none");

    private static final Set<String> ALLOWED_SORT_BY =
            Set.of("name", "mana-value", "price", "rarity");

    private static final Set<String> ALLOWED_EXTRAS = Set.of("mana-cost", "price", "set-symbol");

    public static BuilderDeckLayoutView of(BuilderViewModel builderView,
                                           String viewStyle,
                                           List<String> extrasParams,
                                           List<BuilderDeckSection> deckSections) {
        return of(
                builderView,
                normalizeViewStyle(viewStyle),
                DeckLayoutExtrasFlagsImpl.from(normalizeExtras(extrasParams)),
                deckSections);
    }

    public static BuilderDeckLayoutView of(BuilderViewModel builderView,
                                           String deckViewStyle,
                                           DeckLayoutExtrasFlags deckExtras,
                                           List<BuilderDeckSection> deckSections) {
        String normalizedStyle = normalizeViewStyle(deckViewStyle);
        return BuilderDeckLayoutViewImpl.builder()
                .builderView(builderView)
                .deckViewStyle(normalizedStyle)
                .deckExtras(deckExtras)
                .deckSections(deckSections)
                .deckListCondensed("condensed".equals(normalizedStyle))
                .deckVisualSplit("visual-split".equals(normalizedStyle))
                .deckSpoilerReveal("visual-spoiler".equals(normalizedStyle))
                .build();
    }

    public static String normalizeViewStyle(String viewStyle) {
        return ALLOWED_VIEW_STYLES.contains(viewStyle) ? viewStyle : "text";
    }

    public static String normalizeGroupBy(String groupBy) {
        return ALLOWED_GROUP_BY.contains(groupBy) ? groupBy : "type";
    }

    public static String normalizeSortBy(String sortBy) {
        return ALLOWED_SORT_BY.contains(sortBy) ? sortBy : "name";
    }

    public static Set<String> normalizeExtras(List<String> extrasParams) {
        Set<String> extras = new HashSet<>();
        if (extrasParams != null) {
            for (String chunk : extrasParams) {
                if (chunk != null && ALLOWED_EXTRAS.contains(chunk)) {
                    extras.add(chunk);
                }
            }
        }
        return extras;
    }
}
