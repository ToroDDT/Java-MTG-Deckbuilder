package com.example.mtg_deckbuilder.service.impl;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.views.BuilderDeckSection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** Applies Customize view grouping + sorting before Thymeleaf renders deck sections. */
public final class BuilderDeckLayoutComposer {

    private static final Pattern SPLIT_TYPE_TAGS = Pattern.compile("\\s*[—–-]\\s*");

    /** PostgreSQL text-array literals look like `{W,G}`. */
    private static final Pattern PG_BRACES = Pattern.compile("^\\{\\s*|\\s*}$");
    private static final Pattern SPLIT_ARRAY = Pattern.compile("\\s*,\\s*");

    private static final List<String> RARITY_ORDER =
            List.of("Mythic", "Rare", "Uncommon", "Common", "Special", "Unknown");

    /** Primary row groups for “Group by · Type”; keep aligned with Commander deck conventions. */
    private static final List<String> PRIMARY_TYPE_ORDER = List.of(
            "Creature", "Planeswalker", "Battle", "Instant", "Sorcery", "Enchantment", "Artifact",
            "Land");

    /** Ordered buckets displayed for Mana value grouping. */
    private static final List<String> MANA_BUCKET_ORDER = List.of(
            "Mana value · 0",
            "Mana value · 1",
            "Mana value · 2",
            "Mana value · 3–4",
            "Mana value · 5+",
            "Land",
            "Mana value · unknown / X");

    private static final Comparator<String> NAME_IC =
            Comparator.comparing(String::toLowerCase, String.CASE_INSENSITIVE_ORDER);

    private BuilderDeckLayoutComposer() {
    }

    public static List<BuilderDeckSection> build(String groupBy, String sortBy,
            List<Card> deckCards) {
        Objects.requireNonNull(groupBy);
        Objects.requireNonNull(sortBy);
        List<Card> cards =
                deckCards == null ? List.of() : List.copyOf(deckCards);

        if (cards.isEmpty()) {
            return List.of(new BuilderDeckSection("Deck", List.of()));
        }

        Map<String, List<Card>> buckets = new HashMap<>();
        for (Card card : cards) {
            String key = bucketTitle(groupBy, card);
            buckets.computeIfAbsent(key, k -> new ArrayList<>()).add(card);
        }

        List<String> titleOrder = sectionTitleOrder(groupBy, buckets.keySet());
        Comparator<Card> rowCmp = comparatorFor(sortBy);

        List<BuilderDeckSection> sections = new ArrayList<>();

        LinkedHashSet<String> seenTitles = new LinkedHashSet<>();

        for (String title : titleOrder) {

            appendSection(seenTitles, sections, buckets, title, rowCmp);

        }

        buckets.keySet().stream()


                .filter(t -> !seenTitles.contains(t))


                .sorted(NAME_IC)


                .forEach(extra -> appendSection(seenTitles, sections, buckets, extra, rowCmp));

        return Collections.unmodifiableList(sections);
    }

    private static void appendSection(LinkedHashSet<String> seen,

            List<BuilderDeckSection> out,

            Map<String, List<Card>> buckets,

            String title,

            Comparator<Card> rowCmp) {


        List<Card> rows = buckets.get(title);


        if (rows == null || rows.isEmpty() || seen.contains(title)) {


            return;

        }



        rows.sort(rowCmp);


        seen.add(title);


        out.add(new BuilderDeckSection(title, Collections.unmodifiableList(rows)));

    }



    /** USD string with leading {@code $} — empty string when unavailable. */

    public static String formattedUsd(String numericPlain) {

        if (numericPlain == null || numericPlain.isBlank()) {
            return "";
        }

        try {

            var bd = new BigDecimal(numericPlain.trim());

            return "$" + bd.setScale(2, RoundingMode.HALF_UP).toPlainString();

        } catch (NumberFormatException e) {

            return "";
        }
    }

    private static Comparator<Card> comparatorFor(String sortBy) {

        Comparator<Card> byNameThenEntry =
                Comparator.comparing(Card::getName,
                                Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(c -> Objects.toString(c.getDeckEntryId(), ""));

        return switch (sortBy) {

            case "mana-value" -> Comparator.comparingDouble(
                    BuilderDeckLayoutComposer::manaValueNumericForSort)

                    .thenComparing(byNameThenEntry);


            case "price" ->
                    Comparator.comparingDouble((Card c)
                                    -> BuilderDeckLayoutComposer.parseUsd(c.getPriceUsd()))
                            .reversed()
                            .thenComparing(byNameThenEntry);

            case "rarity" -> Comparator.comparingInt(BuilderDeckLayoutComposer::cardRarityTier)

                    .thenComparing(byNameThenEntry);


            default -> byNameThenEntry;


        };


    }


    /** Lands sorted after spells when Mana value sorting is requested. */
    static double manaValueNumericForSort(Card card) {



        if (card.getTypeLine() != null && card.getTypeLine().contains("Land")) {



            /* Lands occupy dedicated buckets; bury them versus spell rows when grouped together. */


            return 10_000.0;

        }



        Double v = parseCmc(card.getCmc());

        return v != null ? v : 9999;

    }



    /** Parse card mana value as a double row key; ignores missing placeholders. */

    static Double parseCmc(Integer raw) {
        return raw == null ? null : raw.doubleValue();
    }


    static Double parseCmc(String raw) {


        if (raw == null || raw.isBlank()) {

            return null;

        }



        try {

            return Double.parseDouble(raw.trim());


        } catch (NumberFormatException e) {

            return null;

        }

    }



    /** Parse numeric USD helper for comparable rows. */


    static double parseUsd(String raw) {


        if (raw == null || raw.isBlank()) {


            return 0.0;

        }



        try {

            return Double.parseDouble(raw.trim());


        } catch (NumberFormatException e) {

            return 0.0;


        }



    }



    /** Per-row rarity tiers for intra-section sort by rarity. */


    static int cardRarityTier(Card card) {

        return rarityTierOrdinal(card.getRarity());

    }



    /** Map printed rarity strings onto stable ordering ordinals for comparators / buckets. */


    static int rarityTierOrdinal(String rawRarity) {

        return switch ((rawRarity == null ? "" : rawRarity.trim().toLowerCase(Locale.ROOT))) {

            case "", "?", "nan" ->
                    40;

            case "mythic", "m" ->
                    0;

            case "rare", "r" ->
                    10;

            case "uncommon", "u" ->
                    20;


            case "common", "c" ->
                    30;

            case "special", "bonus" ->
                    35;

            default ->
                    42;

        };


    }



    private static String rarityBucketTitle(String rawRarity) {
        if (rawRarity == null || rawRarity.isBlank()) {
            return "Unknown";
        }
        return switch (rawRarity.trim().toLowerCase(Locale.ROOT)) {
            case "mythic", "m" -> "Mythic";
            case "rare", "r" -> "Rare";
            case "uncommon", "u" -> "Uncommon";
            case "common", "c" -> "Common";
            case "special", "bonus" -> "Special";
            default -> capitalizePhrase(rawRarity);
        };
    }



    /** Single heading per card bucket for the modal’s grouping tokens. */


    static String bucketTitle(String groupMode, Card card) {


        return switch (groupMode) {

            case "none" -> "Deck";


            case "type" -> primaryType(card.getTypeLine(), card.getName());


            case "subtype" -> firstSubtypePhrase(card.getTypeLine());


            case "type-tag" -> typeCombinedWithFirstTag(card.getTypeLine());


            case "rarity" -> rarityBucketTitle(card.getRarity());


            case "color", "color-identity", "colors" ->
                    colorIdentityLabel(card.getColorIdentity());


            case "mana-value" -> manaBandTitle(card);


            case "set" ->
                    setLabel(card.getSet());


            case "artist" -> {

                String a = trimmed(card.getArtist());

                yield a.isEmpty() ? "Unknown Artist" : a;

            }

            default ->
                    primaryType(card.getTypeLine(), card.getName());


        };



    }



    /** Insert section titles respecting each grouping UX (fixed order vs lexicographic). */


    static List<String> sectionTitleOrder(String groupMode, Set<String> titles) {


        return switch (groupMode) {


            case "type" ->
                    intersectThenRest(PRIMARY_TYPE_ORDER, titles);



            case "rarity" -> intersectThenRest(RARITY_ORDER, titles);


            case "mana-value" ->
                    intersectThenRest(MANA_BUCKET_ORDER, titles);


            case "subtype", "color", "colors", "color-identity", "set", "artist", "type-tag",

                    "none" -> {




                ArrayList<String> ordered = titles.stream()


                        .sorted(NAME_IC)


                        .collect(Collectors.toCollection(ArrayList::new));


                yield Collections.unmodifiableList(ordered);



            }



            default -> titles.stream()


                    .sorted(NAME_IC)


                    .toList();



        };



    }



    /** Walk {@code canonical} in order while dropping unknown keys missing from {@code haystack}. */


    static List<String> intersectThenRest(List<String> canonicalOrder, Set<String> haystack) {


        LinkedHashSet<String> out = new LinkedHashSet<>();

        for (String t : canonicalOrder) {

            if (haystack.contains(t)) {


                out.add(t);

            }



        }



        haystack.stream()


                .filter(t -> !out.contains(t))


                .sorted(NAME_IC)


                .forEach(out::add);

        return List.copyOf(out);


    }



    /** Classify printable card type headings (singular label). */


    static String primaryType(String typeLine, String nameFallback) {


        if (trimmed(typeLine).isEmpty()) {


            return capitalizePhrase(nameFallback);

        }



        String tl = trimmed(typeLine);

        String low = tl.toLowerCase(Locale.ROOT);


        if (low.contains("creature")) {

            return "Creature";

        }

        if (low.contains("planeswalker")) {


            return "Planeswalker";


        }



        if (low.contains("battle")) {


            return "Battle";


        }



        if (low.contains("instant")) {


            return "Instant";


        }



        if (low.contains("sorcery")) {


            return "Sorcery";


        }



        if (low.contains("enchantment")) {


            return "Enchantment";


        }



        if (low.contains("land")) {

            return "Land";


        }



        if (low.contains("artifact")) {


            return "Artifact";


        }



        /* fallback: uppercase first token */


        String first = SPLIT_TYPE_TAGS.split(tl, 2)[0].trim().split("\\s+", 3)[0];


        return capitalizePhrase(first);

    }



    static String firstSubtypePhrase(String typeLine) {


        if (trimmed(typeLine).isEmpty()) {

            return "General";

        }



        String[] parts = SPLIT_TYPE_TAGS.split(trimmed(typeLine).trim(), 2);


        if (parts.length < 2) {

            return "General";


        }



        String first = SPLIT_ARRAY.split(trimmed(parts[1]))[0].trim();

        return first.isEmpty() ? "General" : first;



    }



    /** “Creature · Human Soldier” headings for “type & tag”. */


    static String typeCombinedWithFirstTag(String typeLine) {

        if (trimmed(typeLine).isEmpty()) {
            return "Unknown";
        }
        String[] parts = SPLIT_TYPE_TAGS.split(trimmed(typeLine), 2);


        String left = capitalizePhrase(parts[0]);


        if (parts.length < 2 || trimmed(parts[1]).isEmpty()) {


            return left;


        }



        String firstTag = SPLIT_ARRAY.split(trimmed(parts[1]))[0].trim();


        String tag = capitalizePhrase(firstTag);

        return left + " · " + tag;



    }



    /** “WUBRG” shorthand color identity headings (Mana symbols order). */


    static String colorIdentityLabel(List<String> colorIdentity) {
        return colorIdentityLabel(colorIdentity == null ? "" : String.join(",", colorIdentity));
    }


    static String colorIdentityLabel(String jdbcLiteralOrEmpty) {


        LinkedHashMap<String, String> uniq = parseColorTokens(jdbcLiteralOrEmpty);


        List<String> order = new ArrayList<>(uniq.keySet());


        order.sort(Comparator.comparingInt((String token)

                        -> BuilderDeckLayoutComposer.manaRingIndex(token))


                .thenComparing(String.CASE_INSENSITIVE_ORDER));


        if (order.isEmpty()) {


            return "Colorless";

        }


        if (uniq.keySet().containsAll(Set.of("W", "U", "B", "R", "G"))) {


            return "Five-color";


        }



        return String.join("", order);


    }



    /** Parse JDBC text/array outputs into normalized single-letter keys. */


    static LinkedHashMap<String, String> parseColorTokens(String raw) {


        LinkedHashMap<String, String> out = new LinkedHashMap<>();


        if (raw == null || raw.trim().isEmpty()) {


            return out;



        }



        String noBraces = PG_BRACES.matcher(raw.trim()).replaceAll("");


        if (noBraces.isEmpty()) {


            return out;

        }



        for (String token : SPLIT_ARRAY.split(noBraces)) {


            String mana = sanitizeManaLetter(token.trim());


            if (!mana.isEmpty()) {


                out.put(mana, mana);

            }


        }



        return out;



    }



    static String sanitizeManaLetter(String fragment) {


        if (fragment == null || fragment.isEmpty()) {


            return "";

        }


        fragment = fragment.replace("\"", "");


        char upperCh = Character.toUpperCase(fragment.charAt(0));

        String letter = Character.toString(upperCh);

        return "WUBRG".contains(letter) ? letter : "";


    }



    /** Scryfall WUBRG index for stable ordering. */


    static int manaRingIndex(String manaLetter) {

        return switch (manaLetter) {


            case "W" -> 0;

            case "U" -> 1;


            case "B" -> 2;

            case "R" -> 3;

            case "G" -> 4;

            default -> 999;

        };



    }



    static String manaBandTitle(Card card) {

        if (trimmed(card.getTypeLine()).contains("Land")) {

            /* Treat land cards specially when bucketed — dry for ramp lands that still tap for mana. */


            return "Land";

        }



        Double cmcDouble = BuilderDeckLayoutComposer.parseCmc(card.getCmc());


        if (cmcDouble == null || cmcDouble.isNaN()) {

            return "Mana value · unknown / X";


        }



        double cmc = Math.max(0, cmcDouble);


        long roundedFloor = Math.round(Math.floor(cmc + 1e-9));


        if (roundedFloor == 0 && cmc <= 1e-5) {


            return "Mana value · 0";


        }


        if (roundedFloor <= 0) {


            return "Mana value · 0";

        }



        switch ((int) roundedFloor) {


            case 1:
                return "Mana value · 1";


            case 2:
                return "Mana value · 2";


            case 3, 4:


                return "Mana value · 3–4";

            default:
                break;

        }

        return "Mana value · 5+";



    }



    static String setLabel(String hintedSetCodeFromSql) {


        String t = trimmed(hintedSetCodeFromSql);

        return t.isEmpty()

                ? "Unknown set"


                : t.toUpperCase(Locale.ROOT);



    }



    /** Title-case-ish helper (“foo bar” becomes “Foo bar”). */


    static String capitalizePhrase(String value) {


        String t = trimmed(value);


        if (t.isEmpty()) {


            return "Unknown";


        }



        String lower = t.toLowerCase(Locale.ROOT);



        char firstChar = Character.toUpperCase(lower.charAt(0));

        return firstChar + lower.substring(1);


    }



    private static String trimmed(String s) {


        return s == null ? "" : s.trim();


    }


}
