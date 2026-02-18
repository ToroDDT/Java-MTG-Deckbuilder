package com.example.mtg_deckbuilder.model;

import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        private int ramp          = 8;
        private int lands         = 35;
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

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getCommander() { return commander; }
        public void setCommander(String commander) { this.commander = commander; }

        public String getPrimaryTag() { return primaryTag; }
        public void setPrimaryTag(String primaryTag) { this.primaryTag = primaryTag; }

        public String getSecondaryTag() { return secondaryTag; }
        public void setSecondaryTag(String secondaryTag) { this.secondaryTag = secondaryTag; }

        public String getTertiaryTag() { return tertiaryTag; }
        public void setTertiaryTag(String tertiaryTag) { this.tertiaryTag = tertiaryTag; }

        public String getTagMode() { return tagMode; }
        public void setTagMode(String tagMode) { this.tagMode = tagMode; }

        public List<String> getCustomThemes() { return customThemes; }
        public void setCustomThemes(List<String> customThemes) { this.customThemes = customThemes; }

        public int getBracket() { return bracket; }
        public void setBracket(int bracket) { this.bracket = bracket; }

        public boolean isPreferCombos() { return preferCombos; }
        public void setPreferCombos(boolean preferCombos) { this.preferCombos = preferCombos; }

        public int getComboCount() { return comboCount; }
        public void setComboCount(int comboCount) { this.comboCount = comboCount; }

        public String getComboBalance() { return comboBalance; }
        public void setComboBalance(String comboBalance) { this.comboBalance = comboBalance; }

        public boolean isEnableMulticopy() { return enableMulticopy; }
        public void setEnableMulticopy(boolean enableMulticopy) { this.enableMulticopy = enableMulticopy; }

        public boolean isUseOwnedOnly() { return useOwnedOnly; }
        public void setUseOwnedOnly(boolean useOwnedOnly) { this.useOwnedOnly = useOwnedOnly; }

        public boolean isPreferOwned() { return preferOwned; }
        public void setPreferOwned(boolean preferOwned) { this.preferOwned = preferOwned; }

        public boolean isSwapMdfcBasics() { return swapMdfcBasics; }
        public void setSwapMdfcBasics(boolean swapMdfcBasics) { this.swapMdfcBasics = swapMdfcBasics; }

        public int getRamp() { return ramp; }
        public void setRamp(int ramp) { this.ramp = ramp; }

        public int getLands() { return lands; }
        public void setLands(int lands) { this.lands = lands; }

        public int getBasicLands() { return basicLands; }
        public void setBasicLands(int basicLands) { this.basicLands = basicLands; }

        public int getCreatures() { return creatures; }
        public void setCreatures(int creatures) { this.creatures = creatures; }

        public int getRemoval() { return removal; }
        public void setRemoval(int removal) { this.removal = removal; }

        public int getWipes() { return wipes; }
        public void setWipes(int wipes) { this.wipes = wipes; }

        public int getCardAdvantage() { return cardAdvantage; }
        public void setCardAdvantage(int cardAdvantage) { this.cardAdvantage = cardAdvantage; }

        public int getProtection() { return protection; }
        public void setProtection(int protection) { this.protection = protection; }

        public String getIncludeCards() { return includeCards; }
        public void setIncludeCards(String includeCards) { this.includeCards = includeCards; }

        public String getExcludeCards() { return excludeCards; }
        public void setExcludeCards(String excludeCards) { this.excludeCards = excludeCards; }

        public String getEnforcementMode() { return enforcementMode; }
        public void setEnforcementMode(String enforcementMode) { this.enforcementMode = enforcementMode; }

        public boolean isAllowIllegal() { return allowIllegal; }
        public void setAllowIllegal(boolean allowIllegal) { this.allowIllegal = allowIllegal; }

        public boolean isFuzzyMatching() { return fuzzyMatching; }
        public void setFuzzyMatching(boolean fuzzyMatching) { this.fuzzyMatching = fuzzyMatching; }

        public boolean isSkipLands() { return skipLands; }
        public void setSkipLands(boolean skipLands) { this.skipLands = skipLands; }

        public boolean isSkipToMisc() { return skipToMisc; }
        public void setSkipToMisc(boolean skipToMisc) { this.skipToMisc = skipToMisc; }

        public boolean isSkipBasics() { return skipBasics; }
        public void setSkipBasics(boolean skipBasics) { this.skipBasics = skipBasics; }

        public boolean isSkipStaples() { return skipStaples; }
        public void setSkipStaples(boolean skipStaples) { this.skipStaples = skipStaples; }

        public boolean isSkipKindred() { return skipKindred; }
        public void setSkipKindred(boolean skipKindred) { this.skipKindred = skipKindred; }

        public boolean isSkipFetches() { return skipFetches; }
        public void setSkipFetches(boolean skipFetches) { this.skipFetches = skipFetches; }

        public boolean isSkipDuals() { return skipDuals; }
        public void setSkipDuals(boolean skipDuals) { this.skipDuals = skipDuals; }

        public boolean isSkipTriomes() { return skipTriomes; }
        public void setSkipTriomes(boolean skipTriomes) { this.skipTriomes = skipTriomes; }

        public boolean isSkipAllCreatures() { return skipAllCreatures; }
        public void setSkipAllCreatures(boolean skipAllCreatures) { this.skipAllCreatures = skipAllCreatures; }

        public boolean isSkipCreaturePrimary() { return skipCreaturePrimary; }
        public void setSkipCreaturePrimary(boolean skipCreaturePrimary) { this.skipCreaturePrimary = skipCreaturePrimary; }

        public boolean isSkipCreatureSecondary() { return skipCreatureSecondary; }
        public void setSkipCreatureSecondary(boolean skipCreatureSecondary) { this.skipCreatureSecondary = skipCreatureSecondary; }

        public boolean isSkipCreatureFill() { return skipCreatureFill; }
        public void setSkipCreatureFill(boolean skipCreatureFill) { this.skipCreatureFill = skipCreatureFill; }

        public boolean isSkipAllSpells() { return skipAllSpells; }
        public void setSkipAllSpells(boolean skipAllSpells) { this.skipAllSpells = skipAllSpells; }

        public boolean isSkipRamp() { return skipRamp; }
        public void setSkipRamp(boolean skipRamp) { this.skipRamp = skipRamp; }

        public boolean isSkipRemoval() { return skipRemoval; }
        public void setSkipRemoval(boolean skipRemoval) { this.skipRemoval = skipRemoval; }

        public boolean isSkipWipes() { return skipWipes; }
        public void setSkipWipes(boolean skipWipes) { this.skipWipes = skipWipes; }

        public boolean isSkipCardAdvantage() { return skipCardAdvantage; }
        public void setSkipCardAdvantage(boolean skipCardAdvantage) { this.skipCardAdvantage = skipCardAdvantage; }

        public boolean isSkipProtection() { return skipProtection; }
        public void setSkipProtection(boolean skipProtection) { this.skipProtection = skipProtection; }

        public boolean isSkipSpellFill() { return skipSpellFill; }
        public void setSkipSpellFill(boolean skipSpellFill) { this.skipSpellFill = skipSpellFill; }

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