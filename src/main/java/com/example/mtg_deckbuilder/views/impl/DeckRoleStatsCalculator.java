package com.example.mtg_deckbuilder.views.impl;

import com.example.mtg_deckbuilder.dto.card.Card;
import com.example.mtg_deckbuilder.views.api.DeckRoleStats;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

final class DeckRoleStatsCalculator {

    private static final Pattern RAMP = Pattern.compile(
            "add \\{[wubrgc0-9/]+\\}|search your library for .{0,40}land|"
                    + "put .{0,60}land .{0,40}onto the battlefield|"
                    + "add one mana|add two mana|add three mana|"
                    + "add an amount of mana equal",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern DRAW = Pattern.compile(
            "draw (?:a |one |two |three |four |\\d+ )?cards?|"
                    + "draws (?:a |one |two |three |four |\\d+ )?cards?|"
                    + "draw cards equal to",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern BOARD_WIPE = Pattern.compile(
            "destroy all|exile all|sacrifice all|"
                    + "all creatures get -|each creature gets -|"
                    + "deals \\d+ damage to each|damage to each creature|"
                    + "each (?:player|opponent|creature) (?:sacrifices|discards|loses|returns)|"
                    + "return all .{0,20} to their owner's hand|"
                    + "each nonland permanent",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern REMOVAL = Pattern.compile(
            "destroy target|exile target|"
                    + "-\\d+/-\\d+.*target|damage to target|"
                    + "fight target|fights target creature|"
                    + "destroy another target|sacrifice target|"
                    + "return target .{0,30} to its owner's hand|"
                    + "counter target",
            Pattern.CASE_INSENSITIVE);

    private DeckRoleStatsCalculator() {
    }

    static DeckRoleStats fromCards(List<Card> cards) {
        if (cards == null || cards.isEmpty()) {
            return DeckRoleStats.EMPTY;
        }

        int ramp = 0;
        int draw = 0;
        int removal = 0;
        int boardWipes = 0;

        for (Card card : cards) {
            if (card == null) {
                continue;
            }
            String oracle = normalizeOracle(card.getOracleText());
            if (oracle.isEmpty()) {
                continue;
            }
            boolean land = isLand(card);

            if (BOARD_WIPE.matcher(oracle).find()) {
                boardWipes++;
            }
            if (!land && REMOVAL.matcher(oracle).find() && !BOARD_WIPE.matcher(oracle).find()) {
                removal++;
            }
            if (DRAW.matcher(oracle).find()) {
                draw++;
            }
            if (matchesRamp(card, oracle, land)) {
                ramp++;
            }
        }

        return new DeckRoleStats(ramp, draw, removal, boardWipes);
    }

    private static boolean matchesRamp(Card card, String oracle, boolean land) {
        if (RAMP.matcher(oracle).find()) {
            if (land && oracle.contains("add {") && !oracle.contains("search your library")) {
                return false;
            }
            return true;
        }
        return !land && oracle.contains("{t}: add {");
    }

    private static boolean isLand(Card card) {
        String typeLine = card.getTypeLine();
        return typeLine != null && typeLine.toLowerCase(Locale.ROOT).contains("land");
    }

    private static String normalizeOracle(String oracleText) {
        if (oracleText == null || oracleText.isBlank()) {
            return "";
        }
        return oracleText.toLowerCase(Locale.ROOT).replace('\n', ' ');
    }
}
