package com.example.mtg_deckbuilder.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Getter
@Setter
public class NewDeckForm {

     // ── Basics ──────────────────────────────────────────────────────────
     private String name;                          // optional deck name

     @jakarta.validation.constraints.NotBlank(message = "Commander is required")
     private String commander;

     // ── Themes ──────────────────────────────────────────────────────────
     private String primaryTag;
     private String secondaryTag;
     private String tertiaryTag;
     private String tagMode = "permissive";        // "permissive" | "strict"

     /** Up to 8 supplemental themes (submitted as repeated hidden inputs) */
     private List<String> customThemes = List.of();

     private int bracket = 3;

     // ── Preferences ─────────────────────────────────────────────────────
     private boolean preferCombos;
     private int     comboCount    = 2;
     private String  comboBalance  = "mix";        // "early" | "late" | "mix"
     private boolean enableMulticopy;
     private boolean useOwnedOnly;
     private boolean preferOwned;
     private boolean swapMdfcBasics;

     // ── Ideal Counts ────────────────────────────────────────────────────
     private int ramp          = 15;
     private int lands         = 38;
     private int basicLands    = 15;
     private int creatures     = 25;
     private int removal       = 10;
     private int wipes         = 2;
     private int cardAdvantage = 10;
     private int protection    = 8;

     // ── Include / Exclude ───────────────────────────────────────────────
     /** Newline-separated card names to force-include */
     private String includeCards;

     /** Newline-separated card names to force-exclude */
     private String excludeCards;

     private String  enforcementMode = "warn";     // "warn" | "strict"
     private boolean allowIllegal;
     private boolean fuzzyMatching;

     // ── Skip Flags ──────────────────────────────────────────────────────
     private boolean skipLands;
     private boolean skipToMisc;
     private boolean skipBasics;
     private boolean skipStaples;
     private boolean skipKindred;
     private boolean skipFetches;
     private boolean skipDuals;
     private boolean skipTriomes;
     private boolean skipAllCreatures;
     private boolean skipCreaturePrimary;
     private boolean skipCreatureSecondary;
     private boolean skipCreatureFill;
     private boolean skipAllSpells;
     private boolean skipRamp;
     private boolean skipRemoval;
     private boolean skipWipes;
     private boolean skipCardAdvantage;
     private boolean skipProtection;
     private boolean skipSpellFill;

     // ── Getters & Setters ────────────────────────────────────────────────

    /** Returns include card names as a parsed list, ignoring blank lines. */
     public List<String> getIncludeCardList() {
          if (includeCards == null || includeCards.isBlank()) return List.of();
          return java.util.Arrays.stream(includeCards.split("\\r?\\n"))
                  .map(String::trim)
                  .filter(s -> !s.isEmpty())
                  .limit(10)
                  .toList();
     }

     /** Returns exclude card names as a parsed list, ignoring blank lines. */
     public List<String> getExcludeCardList() {
          if (excludeCards == null || excludeCards.isBlank()) return List.of();
          return java.util.Arrays.stream(excludeCards.split("\\r?\\n"))
                  .map(String::trim)
                  .filter(s -> !s.isEmpty())
                  .limit(15)
                  .toList();
     }

     /** Returns true if ANY skip flag is set (used to suppress post-adjustment). */
     public boolean hasAnySkipFlag() {
          return skipLands || skipToMisc || skipBasics || skipStaples || skipKindred
                  || skipFetches || skipDuals || skipTriomes
                  || skipAllCreatures || skipCreaturePrimary || skipCreatureSecondary || skipCreatureFill
                  || skipAllSpells || skipRamp || skipRemoval || skipWipes
                  || skipCardAdvantage || skipProtection || skipSpellFill;
     }
}