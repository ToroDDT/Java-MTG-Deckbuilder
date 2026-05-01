package com.example.mtg_deckbuilder.utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DeckOptimizerV2 {

    // ── Instance config ───────────────────────────────────────────────────────
    private final int     deckSize;
    private final int     commanderCost;
    private final boolean beatOtherMode;
    private final int     manaRockCost;
    private final int     drawCost;
    private final int     drawDraw;
    private final double  sixDropValue;

    private final int                  oppCommander;
    private final Map<String, Integer> oppDecklist;

    // Initial search values
    private final int initial1Cmc;
    private final int initial2Cmc;
    private final int initial3Cmc;
    private final int initial4Cmc;
    private final int initial5Cmc;
    private final int initial6Cmc;
    private final int initialRock;
    private final int initialDraw;
    private final int initialLand;

    private final Random rng = new Random();

    // ── Private Constructor ───────────────────────────────────────────────────
    private DeckOptimizerV2(Builder builder) {
        this.initial1Cmc   = builder.initial1Cmc;
        this.initial2Cmc   = builder.initial2Cmc;
        this.initial3Cmc   = builder.initial3Cmc;
        this.initial4Cmc   = builder.initial4Cmc;
        this.initial5Cmc   = builder.initial5Cmc;
        this.initial6Cmc   = builder.initial6Cmc;
        this.initialRock   = builder.initialRock;
        this.initialDraw   = builder.initialDraw;
        this.initialLand   = builder.initialLand;

        this.commanderCost = builder.commanderCost;
        this.manaRockCost  = builder.manaRockCost;
        this.beatOtherMode = builder.beatOtherMode;

        this.oppCommander  = builder.oppCommander;
        this.oppDecklist   = builder.oppDecklist;

        // Hardcoded defaults from original constructor
        this.deckSize      = 99;
        this.drawCost      = 1;
        this.drawDraw      = 3;
        this.sixDropValue  = 6.2;
    }

    // ── Builder Class ─────────────────────────────────────────────────────────
    public static class Builder {
        private int initial1Cmc;
        private int initial2Cmc;
        private int initial3Cmc;
        private int initial4Cmc;
        private int initial5Cmc;
        private int initial6Cmc;
        private int initialRock;
        private int initialDraw;
        private int initialLand;

        private int commanderCost;
        private int manaRockCost;
        private boolean beatOtherMode;

        private int oppCommander;
        private Map<String, Integer> oppDecklist = new HashMap<>();

        public Builder initial1Cmc(int initial1Cmc) { this.initial1Cmc = initial1Cmc; return this; }
        public Builder initial2Cmc(int initial2Cmc) { this.initial2Cmc = initial2Cmc; return this; }
        public Builder initial3Cmc(int initial3Cmc) { this.initial3Cmc = initial3Cmc; return this; }
        public Builder initial4Cmc(int initial4Cmc) { this.initial4Cmc = initial4Cmc; return this; }
        public Builder initial5Cmc(int initial5Cmc) { this.initial5Cmc = initial5Cmc; return this; }
        public Builder initial6Cmc(int initial6Cmc) { this.initial6Cmc = initial6Cmc; return this; }
        public Builder initialRock(int initialRock) { this.initialRock = initialRock; return this; }
        public Builder initialDraw(int initialDraw) { this.initialDraw = initialDraw; return this; }
        public Builder initialLand(int initialLand) { this.initialLand = initialLand; return this; }

        public Builder commanderCost(int commanderCost) { this.commanderCost = commanderCost; return this; }
        public Builder manaRockCost(int manaRockCost) { this.manaRockCost = manaRockCost; return this; }
        public Builder beatOtherMode(boolean beatOtherMode) { this.beatOtherMode = beatOtherMode; return this; }

        public Builder oppCommander(int oppCommander) { this.oppCommander = oppCommander; return this; }
        public Builder oppDecklist(Map<String, Integer> oppDecklist) { this.oppDecklist = oppDecklist; return this; }

        public DeckOptimizerV2 build() {
            return new DeckOptimizerV2(this);
        }
    }

    // ── Library helpers ───────────────────────────────────────────────────────

    private String drawRandomCard(Map<String, Integer> library) {
        int total = library.values().stream().mapToInt(Integer::intValue).sum();
        int roll  = rng.nextInt(total);
        int cumulative = 0;
        for (Map.Entry<String, Integer> e : library.entrySet()) {
            cumulative += e.getValue();
            if (roll < cumulative) {
                e.setValue(e.getValue() - 1);
                return e.getKey();
            }
        }
        throw new IllegalStateException("Library is empty");
    }

    private int nrSpells(Map<String, Integer> h) {
        return h.get("1 CMC") + h.get("2 CMC") + h.get("3 CMC")
             + h.get("4 CMC") + h.get("5 CMC") + h.get("6 CMC")
             + h.get("Rock")  + h.get("Draw");
    }

    private int nrMana(Map<String, Integer> h) {
        return h.get("Land") + h.get("Rock");
    }

    private void putSpellsOnBottom(Map<String, Integer> hand, int toBottom) {
        if (hand.get("Rock") >= 3 || (hand.get("Land") >= 3 && hand.get("Rock") >= 2)) {
            int b = Math.min(hand.get("Rock") - 1, toBottom);
            hand.put("Rock", hand.get("Rock") - b);
            toBottom -= b;
        }
        for (String key : new String[]{"6 CMC","5 CMC","4 CMC","3 CMC","2 CMC","1 CMC"}) {
            int b = Math.min(hand.getOrDefault(key, 0), toBottom);
            hand.put(key, hand.get(key) - b);
            toBottom -= b;
            if (toBottom == 0) return;
        }
        int b = Math.min(hand.get("Draw"), toBottom);
        hand.put("Draw", hand.get("Draw") - b);
        toBottom -= b;
        b = Math.min(hand.get("Rock"), toBottom);
        hand.put("Rock", hand.get("Rock") - b);
    }

    private Map<String, Integer> newHand() {
        Map<String, Integer> h = new LinkedHashMap<>();
        for (String k : new String[]{"1 CMC","2 CMC","3 CMC","4 CMC","5 CMC","6 CMC","Rock","Sol Ring","Draw","Land"})
            h.put(k, 0);
        return h;
    }

    // ── Single simulation ─────────────────────────────────────────────────────
    private double runOneSim(boolean solRingInOpener, Map<String, Integer> decklist, int commanderCost) {
        int    landsInPlay           = 0;
        int    rocksInPlay           = 0;
        int    commanderSphereInPlay = 0;
        double compoundedManaSpent   = 0;
        double cumulativeManaInPlay  = 0;

        Map<String, Integer> hand    = newHand();
        Map<String, Integer> library = new LinkedHashMap<>(decklist);
        boolean keephand = false;

        int[] handsizes = {0, 7, 6, 5, 4};
        for (int hs : handsizes) {
            if (keephand) break;
            library = new LinkedHashMap<>(decklist);
            hand    = newHand();

            if (hs == 0) {
                if (solRingInOpener) {
                    library.put("Sol Ring", library.get("Sol Ring") - 1);
                    hand.put("Sol Ring", 1);
                    for (int i = 0; i < 6; i++) hand.merge(drawRandomCard(library), 1, Integer::sum);
                } else {
                    library.put("Sol Ring", library.get("Sol Ring") - 1);
                    for (int i = 0; i < 7; i++) hand.merge(drawRandomCard(library), 1, Integer::sum);
                    library.put("Sol Ring", library.getOrDefault("Sol Ring", 0) + 1);
                }
                if ((hand.get("Land") >= 3 && hand.get("Land") <= 5 && nrMana(hand) <= 5)
                 || (hand.get("Land") >= 1 && hand.get("Land") <= 5 && hand.get("Sol Ring") == 1))
                    keephand = true;

            } else {
                for (int i = 0; i < 7; i++) hand.merge(drawRandomCard(library), 1, Integer::sum);

                if (hs == 7) {
                    if ((hand.get("Land") >= 2 && hand.get("Land") <= 5 && nrMana(hand) <= 5)
                     || (hand.get("Land") >= 1 && hand.get("Land") <= 5 && hand.get("Sol Ring") == 1))
                        keephand = true;
                } else if (hs == 6) {
                    if (nrSpells(hand) > 3) putSpellsOnBottom(hand, 1);
                    else hand.put("Land", hand.get("Land") - 1);
                    if ((hand.get("Land") >= 2 && hand.get("Land") <= 4)
                     || (hand.get("Land") >= 1 && hand.get("Sol Ring") == 1))
                        keephand = true;
                } else if (hs == 5) {
                    if      (nrSpells(hand) > 3)  putSpellsOnBottom(hand, 2);
                    else if (nrSpells(hand) == 3) { hand.put("Land", hand.get("Land") - 1); putSpellsOnBottom(hand, 1); }
                    else                            hand.put("Land", hand.get("Land") - 2);
                    if ((hand.get("Land") >= 2 && hand.get("Land") <= 4)
                     || (hand.get("Land") >= 1 && hand.get("Sol Ring") == 1))
                        keephand = true;
                } else {
                    if      (nrSpells(hand) > 3)  putSpellsOnBottom(hand, 3);
                    else if (nrSpells(hand) == 3) { hand.put("Land", hand.get("Land") - 1); putSpellsOnBottom(hand, 2); }
                    else if (nrSpells(hand) == 2) { hand.put("Land", hand.get("Land") - 2); putSpellsOnBottom(hand, 1); }
                    else                            hand.put("Land", hand.get("Land") - 3);
                    keephand = true;
                }
            }
        }

        hand.merge(commanderCost + " CMC", 1, Integer::sum);

        for (int turn = 1; turn <= 7; turn++) {
            compoundedManaSpent += cumulativeManaInPlay;
            hand.merge(drawRandomCard(library), 1, Integer::sum);

            boolean landPlayed = false;
            if (hand.get("Land") > 0) {
                hand.put("Land", hand.get("Land") - 1);
                landsInPlay++;
                landPlayed = true;
            }

            int manaAvailable     = landsInPlay + rocksInPlay;
            int manaAtStartOfTurn = manaAvailable;
            boolean castNonRock   = false;

            if (turn == 1 && manaAvailable >= 1 && hand.get("Sol Ring") == 1) {
                hand.put("Sol Ring", 0);
                rocksInPlay += 2;
                if (hand.get("Rock") >= 1 && manaRockCost == 2) {
                    hand.put("Rock", hand.get("Rock") - 1);
                    rocksInPlay++;
                }
                manaAvailable = 0;
            }
            if (turn >= 2 && manaAvailable >= 1 && hand.get("Sol Ring") == 1) {
                hand.put("Sol Ring", 0);
                manaAvailable += 1;
                rocksInPlay += 2;
            }

            if ((turn == 1 || turn == 2) && manaRockCost == 1) {
                int cast = Math.min(hand.get("Rock"), manaAvailable);
                hand.put("Rock", hand.get("Rock") - cast);
                manaAvailable -= cast;
                rocksInPlay   += cast;
            }
            if (turn == 2 && manaRockCost == 2) {
                int cast = Math.min(hand.get("Rock"), manaAvailable / 2);
                hand.put("Rock", hand.get("Rock") - cast);
                manaAvailable -= cast * 2;
                manaAvailable += cast;
                rocksInPlay   += cast;
            }
            if (turn == 3 && manaRockCost == 3) {
                int cast = Math.min(hand.get("Rock"), manaAvailable / 3);
                hand.put("Rock", hand.get("Rock") - cast);
                manaAvailable -= cast * 3;
                manaAvailable += cast;
                rocksInPlay           += cast;
                commanderSphereInPlay += cast;
            }

            if ((turn == 3 || turn == 4) && manaAvailable >= Math.max(2, manaRockCost) && manaAvailable <= 7) {
                int followCmc = (manaRockCost == 2 || manaRockCost == 3)
                    ? manaAvailable + 1 - manaRockCost : manaAvailable - 1;
                String followKey = followCmc + " CMC";
                if (followCmc >= 1 && followCmc <= 6 && hand.get("Rock") >= 1 && hand.getOrDefault(followKey, 0) >= 1) {
                    hand.put("Rock", hand.get("Rock") - 1);
                    manaAvailable += (manaRockCost == 2 || manaRockCost == 3) ? 1 - manaRockCost : -1;
                    rocksInPlay++;
                    hand.put(followKey, hand.get(followKey) - 1);
                    manaAvailable -= followCmc;
                    compoundedManaSpent  += followCmc;
                    cumulativeManaInPlay += followCmc;
                    castNonRock = true;
                }
            }

            if (manaAvailable >= 3 && manaAvailable <= 6 && hand.getOrDefault(manaAvailable + " CMC", 0) == 0) {
                int loAmt = hand.getOrDefault("2 CMC", 0);
                int hiCmc = manaAvailable - 2;
                int hiAmt = hand.getOrDefault(hiCmc + " CMC", 0);
                boolean distinct = (hiCmc != 2) ? (loAmt >= 1 && hiAmt >= 1) : (loAmt >= 2);
                if (distinct) {
                    hand.put("2 CMC", loAmt - 1);
                    hand.put(hiCmc + " CMC", hand.get(hiCmc + " CMC") - 1);
                    compoundedManaSpent  += manaAvailable;
                    cumulativeManaInPlay += manaAvailable;
                    manaAvailable = 0;
                    castNonRock = true;
                }
            }

            double[] after = castSpellsTracked(hand, manaAvailable, compoundedManaSpent, cumulativeManaInPlay);
            manaAvailable        = (int) after[0];
            compoundedManaSpent  = after[1];
            cumulativeManaInPlay = after[2];
            if (after[3] > 0) castNonRock = true;

            int castRock = Math.min(hand.get("Rock"), manaAvailable / manaRockCost);
            hand.put("Rock", hand.get("Rock") - castRock);
            manaAvailable -= castRock * manaRockCost;
            if (manaRockCost == 2 || manaRockCost == 3) manaAvailable += castRock;
            rocksInPlay += castRock;
            if (manaRockCost == 3) commanderSphereInPlay += castRock;

            if (manaRockCost == 2 && manaAtStartOfTurn >= 2 && manaAvailable == 1 && hand.get("Rock") >= 1 && castNonRock) {
                hand.put("Rock", hand.get("Rock") - 1);
                rocksInPlay++;
                manaAvailable = -1;
            }
            if (manaRockCost == 3 && manaAtStartOfTurn >= 3 && manaAvailable == 2 && hand.get("Rock") >= 1 && castNonRock) {
                hand.put("Rock", hand.get("Rock") - 1);
                rocksInPlay++;
                commanderSphereInPlay++;
                manaAvailable = -2;
            }

            boolean drawCondition   = drawCost <= manaAvailable && hand.get("Draw") >= 1;
            boolean sphereCondition = commanderSphereInPlay >= 1 && manaAvailable >= 6;
            if (drawCondition || sphereCondition) {
                if (drawCondition) {
                    hand.put("Draw", hand.get("Draw") - 1);
                    manaAvailable -= drawCost;
                    for (int d = 0; d < drawDraw; d++) hand.merge(drawRandomCard(library), 1, Integer::sum);
                }
                if (sphereCondition) {
                    rocksInPlay--;
                    commanderSphereInPlay--;
                    hand.merge(drawRandomCard(library), 1, Integer::sum);
                }
                if (!landPlayed && hand.get("Land") >= 1) {
                    hand.put("Land", hand.get("Land") - 1);
                    landsInPlay++;
                    manaAvailable++;
                }
                if (manaAvailable >= 1 && hand.get("Sol Ring") == 1) {
                    hand.put("Sol Ring", 0);
                    manaAvailable += 1;
                    rocksInPlay += 2;
                }
                double[] afterDraw = castSpellsTracked(hand, manaAvailable, compoundedManaSpent, cumulativeManaInPlay);
                manaAvailable        = (int) afterDraw[0];
                compoundedManaSpent  = afterDraw[1];
                cumulativeManaInPlay = afterDraw[2];

                castRock = Math.min(hand.get("Rock"), manaAvailable / manaRockCost);
                hand.put("Rock", hand.get("Rock") - castRock);
                manaAvailable -= castRock * manaRockCost;
                if (manaRockCost == 2 || manaRockCost == 3) manaAvailable += castRock;
                rocksInPlay += castRock;
            }
        }

        return compoundedManaSpent;
    }

    private double[] castSpellsTracked(Map<String, Integer> hand, int manaAvailable,
                                       double compoundedManaSpent, double cumulativeManaInPlay) {
        double castAny = 0;
        double[] cmcValues = {sixDropValue, 5, 4, 3, 2, 1};
        int[]    cmcCosts  = {6, 5, 4, 3, 2, 1};
        for (int i = 0; i < cmcCosts.length; i++) {
            String key = cmcCosts[i] + " CMC";
            int castable = Math.min(hand.getOrDefault(key, 0), manaAvailable / cmcCosts[i]);
            if (castable > 0) {
                hand.put(key, hand.get(key) - castable);
                manaAvailable        -= castable * cmcCosts[i];
                compoundedManaSpent  += castable * cmcValues[i];
                cumulativeManaInPlay += castable * cmcValues[i];
                castAny = 1;
            }
        }
        return new double[]{manaAvailable, compoundedManaSpent, cumulativeManaInPlay, castAny};
    }

    // ── Run optimizer ─────────────────────────────────────────────────────────
    public void run() {
        int total = initial1Cmc + initial2Cmc + initial3Cmc + initial4Cmc
                  + initial5Cmc + initial6Cmc + initialRock + initialDraw + initialLand;
        if (total != deckSize - 1) {
            System.out.printf("Error: values must sum to %d (got %d). Sol Ring fills the last slot.%n",
                deckSize - 1, total);
            return;
        }

        int numSims = 10;

        int bestOne   = initial1Cmc, bestTwo   = initial2Cmc, bestThree = initial3Cmc;
        int bestFour  = initial4Cmc, bestFive  = initial5Cmc, bestSix   = initial6Cmc;
        int bestRock  = initialRock, bestDraw  = initialDraw, bestLand  = initialLand;

        int newBestOne = bestOne, newBestTwo = bestTwo, newBestThree = bestThree;
        int newBestFour = bestFour, newBestFive = bestFive, newBestSix = bestSix;
        int newBestRock = bestRock, newBestDraw = bestDraw, newBestLand = bestLand;

        double previousBestManaSpent = 0;
        int    previousSimsForBest   = 0;
        int    simsForBest           = 0;
        boolean continueSearching    = true;

        Map<String, Double>  estimation = new HashMap<>();
        Map<String, Integer> numberSims = new HashMap<>();

        while (continueSearching) {
            double bestManaSpent = 0;

            List<Double> oppResults = new ArrayList<>();
            if (beatOtherMode) {
                int oppSims = numSims / 10;
                double solRingProb = 7.0 / 99.0;
                for (int i = 0; i < oppSims; i++) {
                    double opp1 = runOneSim(rng.nextDouble() <= solRingProb, oppDecklist, oppCommander);
                    double opp2 = runOneSim(rng.nextDouble() <= solRingProb, oppDecklist, oppCommander);
                    double opp3 = runOneSim(rng.nextDouble() <= solRingProb, oppDecklist, oppCommander);
                    oppResults.add(Math.max(opp1, Math.max(opp2, opp3)));
                }
            }

            for (int one   = Math.max(bestOne-1,0);   one   <= bestOne+1;   one++)
            for (int two   = Math.max(bestTwo-1,0);   two   <= bestTwo+1;   two++)
            for (int three = Math.max(bestThree-1,0); three <= bestThree+1; three++)
            for (int four  = Math.max(bestFour-1,0);  four  <= bestFour+1;  four++)
            for (int five  = Math.max(bestFive-1,0);  five  <= bestFive+1;  five++)
            for (int six   = Math.max(bestSix-1,0);   six   <= bestSix+1;   six++)
            for (int rock  = Math.max(bestRock-1,0);  rock  <= bestRock+1;  rock++)
            for (int land  = Math.max(bestLand-1,0);  land  <= bestLand+1;  land++) {
                int draw = 0;
                int tot  = one+two+three+four+five+six+rock+draw+land;
                int nrChanges = Math.abs(one-bestOne)+Math.abs(two-bestTwo)+Math.abs(three-bestThree)
                              + Math.abs(four-bestFour)+Math.abs(five-bestFive)+Math.abs(six-bestSix)
                              + Math.abs(rock-bestRock)+Math.abs(draw-bestDraw)+Math.abs(land-bestLand);

                boolean inNeighborhood = previousSimsForBest < 150_000
                    ? (tot == deckSize - 1 && nrChanges <= 2)
                    : (tot == deckSize - 1);
                if (!inNeighborhood) continue;

                String key = one+","+two+","+three+","+four+","+five+","+six+","+rock+","+draw+","+land;
                estimation.putIfAbsent(key, 0.0);
                numberSims.putIfAbsent(key, 0);

                int    prevN = numberSims.get(key);
                double prevE = estimation.get(key);

                if ((prevN > 50_000  && prevE < 0.998  * previousBestManaSpent) ||
                    (prevN > 100_000 && prevE < 0.999  * previousBestManaSpent) ||
                    (prevN > 200_000 && prevE < 0.9995 * previousBestManaSpent)) continue;

                Map<String, Integer> decklist = new LinkedHashMap<>();
                decklist.put("1 CMC",    one);   decklist.put("2 CMC",    two);
                decklist.put("3 CMC",    three); decklist.put("4 CMC",    four);
                decklist.put("5 CMC",    five);  decklist.put("6 CMC",    six);
                decklist.put("Rock",     rock);  decklist.put("Sol Ring", 1);
                decklist.put("Draw",     draw);  decklist.put("Land",     land);

                double totalMana = 0;
                int luckyGames   = (int)(numSims * 7.0 / 99.0);
                int unluckyGames = numSims - luckyGames;

                for (int i = 0; i < luckyGames; i++) {
                    double result = runOneSim(true, decklist, commanderCost);
                    if (beatOtherMode) { if (result > oppResults.get(i / 10)) totalMana += 1; }
                    else totalMana += result;
                }
                for (int i = 0; i < unluckyGames; i++) {
                    double result = runOneSim(false, decklist, commanderCost);
                    if (beatOtherMode) { if (result > oppResults.get((luckyGames + i) / 10)) totalMana += 1; }
                    else totalMana += result;
                }

                double avg = Math.round(totalMana / numSims * 10000.0) / 10000.0;
                int    newN = prevN + numSims;
                double newE = Math.round(((prevE * prevN) + (avg * numSims)) / newN * 10000.0) / 10000.0;
                numberSims.put(key, newN);
                estimation.put(key, newE);

                boolean samePrev = one==bestOne && two==bestTwo && three==bestThree && four==bestFour
                                && five==bestFive && six==bestSix && rock==bestRock;
                if (newE >= bestManaSpent) {
                    String tag = samePrev ? "Update!" : newE >= previousBestManaSpent ? "Improv!" : "-------";
                    System.out.printf("---%s Deck %d,%d,%d,%d,%d,%d | %d,%d,%d  prev=%.4f/%d  now=%.4f/%d%n",
                        tag, one, two, three, four, five, six, draw, rock, land, prevE, prevN, newE, newN);
                    bestManaSpent = newE;
                    newBestOne=one; newBestTwo=two; newBestThree=three; newBestFour=four;
                    newBestFive=five; newBestSix=six; newBestRock=rock; newBestDraw=draw; newBestLand=land;
                    simsForBest = newN;
                } else if (newE < previousBestManaSpent && newE > 0.998 * bestManaSpent) {
                    String tag = samePrev ? "Update!" : "Close! ";
                    System.out.printf("---%s Deck %d,%d,%d,%d,%d,%d | %d,%d,%d  prev=%.4f/%d  now=%.4f/%d%n",
                        tag, one, two, three, four, five, six, draw, rock, land, prevE, prevN, newE, newN);
                }
            }

            boolean prevStillBest = newBestOne==bestOne && newBestTwo==bestTwo && newBestThree==bestThree
                                 && newBestFour==bestFour && newBestFive==bestFive && newBestSix==bestSix
                                 && newBestRock==bestRock && newBestDraw==bestDraw;
            previousBestManaSpent = bestManaSpent;
            continueSearching = !(prevStillBest && simsForBest > 200_000);

            bestOne=newBestOne; bestTwo=newBestTwo; bestThree=newBestThree; bestFour=newBestFour;
            bestFive=newBestFive; bestSix=newBestSix; bestRock=newBestRock; bestDraw=newBestDraw; bestLand=newBestLand;

            boolean override = false;
            for (Map.Entry<String, Double> e : estimation.entrySet()) {
                if (e.getValue() > bestManaSpent && numberSims.get(e.getKey()) >= previousSimsForBest / 2) {
                    bestManaSpent = e.getValue();
                    String[] parts = e.getKey().split(",");
                    bestOne=Integer.parseInt(parts[0]); bestTwo=Integer.parseInt(parts[1]);
                    bestThree=Integer.parseInt(parts[2]); bestFour=Integer.parseInt(parts[3]);
                    bestFive=Integer.parseInt(parts[4]); bestSix=Integer.parseInt(parts[5]);
                    bestRock=Integer.parseInt(parts[6]); bestDraw=Integer.parseInt(parts[7]); bestLand=Integer.parseInt(parts[8]);
                    simsForBest = numberSims.get(e.getKey());
                    override = true;
                    continueSearching = true;
                }
            }
            if (override) System.out.println("!!!!!!!!!OVERRIDE!!!!!!!");

            numSims += 1000;
            previousSimsForBest = simsForBest;
            System.out.printf("====> Deck: %d one, %d two, %d three, %d four, %d five, %d six, %d draw, %d rock, 1 Sol Ring, %d lands ==> %.4f%n",
                bestOne, bestTwo, bestThree, bestFour, bestFive, bestSix, bestDraw, bestRock, bestLand, bestManaSpent);
        }
    }
}