package com.example.mtg_deckbuilder.utils;
import java.util.*;

public class DeckOptimizer {

    // ── Config ────────────────────────────────────────────────────────────────
    static final int DECK_SIZE       = 99;
    static final int COMMANDER_COST  = 4;
    static final int INITIAL_ROCK    = 10;
    static final int INITIAL_1_CMC   = 7;
    static final int INITIAL_2_CMC   = 10;
    static final int INITIAL_3_CMC   = 10;
    static final int INITIAL_4_CMC   = 14;
    static final int INITIAL_5_CMC   = 9;
    static final int INITIAL_6_CMC   = 0;
    static final int INITIAL_LAND    = 40;
    static final int INITIAL_DRAW    = 0;
    static final boolean DEBUG_MODE  = false;

    static final int DRAW_COST = 4;
    static final int DRAW_DRAW = 3;

    static final Random RNG = new Random();

    // ── Decklist (set before each sim) ───────────────────────────────────────
    static Map<String, Integer> decklist = new HashMap<>();

    // ── Hand helper ──────────────────────────────────────────────────────────
    static int nrSpells(Map<String, Integer> h) {
        return h.get("1 CMC") + h.get("2 CMC") + h.get("3 CMC")
             + h.get("4 CMC") + h.get("5 CMC") + h.get("6 CMC")
             + h.get("Rock")  + h.get("Draw");
    }

    static int nrMana(Map<String, Integer> h) {
        return h.get("Land") + h.get("Rock");
    }

    static void putSpellsOnBottom(Map<String, Integer> hand, int toBottom) {
        // Bottom excess rocks first if hand is mana-flooded
        if (hand.get("Rock") >= 3 || (hand.get("Land") >= 3 && hand.get("Rock") >= 2)) {
            int b = Math.min(hand.get("Rock") - 1, toBottom);
            hand.put("Rock", hand.get("Rock") - b);
            toBottom -= b;
        }
        for (String key : new String[]{"6 CMC","5 CMC","4 CMC","3 CMC","2 CMC","1 CMC"}) {
            int b = Math.min(hand.get(key), toBottom);
            hand.put(key, hand.get(key) - b);
            toBottom -= b;
            if (toBottom == 0) return;
        }
        // Card advantage (Draw) is bottomed last
        int b = Math.min(hand.get("Draw"), toBottom);
        hand.put("Draw", hand.get("Draw") - b);
        toBottom -= b;
        // Safety valve for all-land / all-rock hands
        b = Math.min(hand.get("Rock"), toBottom);
        hand.put("Rock", hand.get("Rock") - b);
    }

    // ── Single simulation ─────────────────────────────────────────────────────
    static double[] runOneSim() {
        int landsInPlay  = 0;
        int rocksInPlay  = 0;
        double compoundedManaSpent  = 0;
        double cumulativeManaInPlay = 0;
        int turnOfInterest = 7;

        // Build and shuffle library
        List<String> library = new ArrayList<>();
        for (Map.Entry<String, Integer> e : decklist.entrySet())
            for (int i = 0; i < e.getValue(); i++)
                library.add(e.getKey());
        Collections.shuffle(library, RNG);

        // Opening hand + mulligan
        Map<String, Integer> hand = newHand();
        boolean keephand = false;

        int[] handsizes = {-7, 7, 6, 5, 4}; // -7 = free mull
        for (int hs : handsizes) {
            if (keephand) break;

            // Re-shuffle library and draw 7
            Collections.shuffle(library, RNG);
            hand = newHand();
            for (int i = 0; i < 7; i++) hand.merge(library.remove(0), 1, Integer::sum);

            if (DEBUG_MODE) System.out.println("Opening hand: " + hand);

            if (hs == -7) { // free mulligan
                if ((hand.get("Land") >= 3 && hand.get("Land") <= 5 && nrMana(hand) <= 5)
                 || (hand.get("Land") >= 1 && hand.get("Land") <= 5 && hand.get("Sol Ring") == 1))
                    keephand = true;

            } else if (hs == 7) {
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
                if (nrSpells(hand) > 3)      putSpellsOnBottom(hand, 2);
                else if (nrSpells(hand) == 3) { hand.put("Land", hand.get("Land") - 1); putSpellsOnBottom(hand, 1); }
                else                           hand.put("Land", hand.get("Land") - 2);
                if ((hand.get("Land") >= 2 && hand.get("Land") <= 4)
                 || (hand.get("Land") >= 1 && hand.get("Sol Ring") == 1))
                    keephand = true;

            } else { // 4
                if (nrSpells(hand) > 3)      putSpellsOnBottom(hand, 3);
                else if (nrSpells(hand) == 3) { hand.put("Land", hand.get("Land") - 1); putSpellsOnBottom(hand, 2); }
                else if (nrSpells(hand) == 2) { hand.put("Land", hand.get("Land") - 2); putSpellsOnBottom(hand, 1); }
                else                           hand.put("Land", hand.get("Land") - 3);
                keephand = true;
            }
        }

        // Add commander as a free spell
        hand.merge(COMMANDER_COST + " CMC", 1, Integer::sum);

        // ── Turn loop ─────────────────────────────────────────────────────────
        for (int turn = 1; turn <= turnOfInterest; turn++) {
            compoundedManaSpent += cumulativeManaInPlay;

            // Draw for turn
            hand.merge(library.remove(0), 1, Integer::sum);

            // Play a land
            boolean landPlayed = false;
            if (hand.get("Land") > 0) {
                hand.put("Land", hand.get("Land") - 1);
                landsInPlay++;
                landPlayed = true;
            }

            int manaAvailable = landsInPlay + rocksInPlay;
            int manaAtStartOfTurn = manaAvailable;
            boolean castNonRockThisTurn = false;

            // ── Turn 1 Sol Ring ──
            if (turn == 1) {
                if (manaAvailable >= 1 && hand.get("Sol Ring") == 1) {
                    hand.put("Sol Ring", 0);
                    rocksInPlay += 2;
                    if (hand.get("Rock") >= 1) { hand.put("Rock", hand.get("Rock") - 1); rocksInPlay++; }
                    manaAvailable = 0;
                }
            }

            // ── Turn 2+ Sol Ring ──
            if (turn >= 2 && manaAvailable >= 1 && hand.get("Sol Ring") == 1) {
                hand.put("Sol Ring", 0);
                manaAvailable++;   // costs 1, produces 2 → net +1
                rocksInPlay += 2;
            }

            // ── Turn 2: deploy rocks ──
            if (turn == 2) {
                int castRock = Math.min(hand.get("Rock"), manaAvailable / 2);
                hand.put("Rock", hand.get("Rock") - castRock);
                manaAvailable -= castRock * 2;
                manaAvailable += castRock;
                rocksInPlay   += castRock;
            }

            // ── Turn 3-4: rock + follow-up ──
            if ((turn == 3 || turn == 4) && manaAvailable >= 2 && manaAvailable <= 7) {
                int followCmc = manaAvailable - 1;
                String followKey = followCmc + " CMC";
                if (hand.get("Rock") >= 1 && hand.getOrDefault(followKey, 0) >= 1) {
                    hand.put("Rock", hand.get("Rock") - 1);
                    manaAvailable--;
                    rocksInPlay++;
                    hand.put(followKey, hand.get(followKey) - 1);
                    manaAvailable -= followCmc;
                    compoundedManaSpent  += followCmc;
                    cumulativeManaInPlay += followCmc;
                    castNonRockThisTurn = true;
                }
            }

            // ── Two-spell split (e.g. 2+3 on 5 mana) ──
            if (manaAvailable >= 3 && manaAvailable <= 6) {
                String topKey = manaAvailable + " CMC";
                if (hand.getOrDefault(topKey, 0) == 0) {
                    String loKey = "2 CMC";
                    String hiKey = (manaAvailable - 2) + " CMC";
                    int loAmt = hand.getOrDefault(loKey, 0);
                    int hiAmt = hand.getOrDefault(hiKey, 0);
                    if (loAmt >= 1 && hiAmt >= 1 && loAmt + hiAmt >= 2) {
                        hand.put(loKey, loAmt - 1);
                        hand.put(hiKey, hiAmt - 1);
                        compoundedManaSpent  += manaAvailable;
                        cumulativeManaInPlay += manaAvailable;
                        manaAvailable = 0;
                        castNonRockThisTurn = true;
                    }
                }
            }

            // ── Cast spells greedily, highest CMC first ──
            double[] cmcValues = {6.2, 5, 4, 3, 2, 1};
            int[]    cmcCosts  = {6,   5, 4, 3, 2, 1};
            for (int i = 0; i < cmcCosts.length; i++) {
                String key = cmcCosts[i] + " CMC";
                int castable = Math.min(hand.getOrDefault(key, 0), manaAvailable / cmcCosts[i]);
                hand.put(key, hand.getOrDefault(key, 0) - castable);
                manaAvailable        -= castable * cmcCosts[i];
                compoundedManaSpent  += castable * cmcValues[i];
                cumulativeManaInPlay += castable * cmcValues[i];
                if (castable > 0) castNonRockThisTurn = true;
            }

            // ── Late rocks ──
            int castRock = Math.min(hand.get("Rock"), manaAvailable / 2);
            hand.put("Rock", hand.get("Rock") - castRock);
            manaAvailable -= castRock * 2;
            manaAvailable += castRock;
            rocksInPlay   += castRock;

            // ── Retroactive rock squeeze ──
            if (manaAtStartOfTurn >= 2 && manaAvailable == 1 && hand.get("Rock") >= 1 && castNonRockThisTurn) {
                hand.put("Rock", hand.get("Rock") - 1);
                rocksInPlay++;
            }

            // ── Card draw spells ──
            if (DRAW_COST <= manaAvailable && hand.get("Draw") >= 1) {
                hand.put("Draw", hand.get("Draw") - 1);
                manaAvailable -= DRAW_COST;
                for (int d = 0; d < DRAW_DRAW; d++)
                    hand.merge(library.remove(0), 1, Integer::sum);
                if (!landPlayed && hand.get("Land") >= 1) {
                    hand.put("Land", hand.get("Land") - 1);
                    landsInPlay++;
                    manaAvailable++;
                }
            }
        }

        int lucky = (hand.getOrDefault("Sol Ring", 0) == 0 && rocksInPlay >= 2) ? 1 : 0;
        return new double[]{compoundedManaSpent, lucky};
    }

    static Map<String, Integer> newHand() {
        Map<String, Integer> h = new LinkedHashMap<>();
        for (String k : new String[]{"1 CMC","2 CMC","3 CMC","4 CMC","5 CMC","6 CMC","Rock","Sol Ring","Draw","Land"})
            h.put(k, 0);
        return h;
    }

    // ── Main: local search optimizer ─────────────────────────────────────────
    public static void main(String[] args) {
        int numSims = 10_000;

        int bestOne   = INITIAL_1_CMC, bestTwo  = INITIAL_2_CMC, bestThree = INITIAL_3_CMC;
        int bestFour  = INITIAL_4_CMC, bestFive = INITIAL_5_CMC, bestSix   = INITIAL_6_CMC;
        int bestRock  = INITIAL_ROCK,  bestLand = INITIAL_LAND,  bestDraw  = INITIAL_DRAW;

        double previousBestManaSpent = 0;
        int    previousSimsForBest   = 0;
        int    simsForBest           = 0;
        boolean continueSearching    = true;

        Map<String, Double> estimation  = new HashMap<>();
        Map<String, Integer> numberSims = new HashMap<>();

        while (continueSearching) {
            double bestManaSpent = 0;
            int newBestOne = bestOne, newBestTwo = bestTwo, newBestThree = bestThree;
            int newBestFour = bestFour, newBestFive = bestFive, newBestSix = bestSix;
            int newBestRock = bestRock, newBestDraw = bestDraw, newBestLand = bestLand;

            for (int one   = Math.max(bestOne-1,0);   one   <= bestOne+1;   one++)
            for (int two   = Math.max(bestTwo-1,0);   two   <= bestTwo+1;   two++)
            for (int three = Math.max(bestThree-1,0); three <= bestThree+1; three++)
            for (int four  = Math.max(bestFour-1,0);  four  <= bestFour+1;  four++)
            for (int five  = Math.max(bestFive-1,0);  five  <= bestFive+1;  five++)
            for (int six   = Math.max(bestSix-1,0);   six   <= bestSix+1;   six++)
            for (int rock  = Math.max(bestRock-1,0);  rock  <= bestRock+1;  rock++)
            for (int land  = Math.max(bestLand-1,0);  land  <= bestLand+1;  land++) {
                int draw = 0;
                int total = one+two+three+four+five+six+rock+draw+land;
                int nrChanges = Math.abs(one-bestOne)+Math.abs(two-bestTwo)+Math.abs(three-bestThree)
                              + Math.abs(four-bestFour)+Math.abs(five-bestFive)+Math.abs(six-bestSix)
                              + Math.abs(rock-bestRock)+Math.abs(land-bestLand);

                boolean inNeighborhood = previousSimsForBest < 150_000
                    ? (total == DECK_SIZE - 1 && nrChanges <= 2)
                    : (total == DECK_SIZE - 1);
                if (!inNeighborhood) continue;

                String key = one+","+two+","+three+","+four+","+five+","+six+","+rock+","+draw+","+land;
                estimation .putIfAbsent(key, 0.0);
                numberSims .putIfAbsent(key, 0);

                int    prevN = numberSims.get(key);
                double prevE = estimation.get(key);

                // Skip obviously bad decks
                if ((prevN > 50_000  && prevE < 0.998  * previousBestManaSpent) ||
                    (prevN > 100_000 && prevE < 0.999  * previousBestManaSpent) ||
                    (prevN > 200_000 && prevE < 0.9995 * previousBestManaSpent)) continue;

                // Set decklist for this configuration
                decklist.clear();
                decklist.put("1 CMC",   one);
                decklist.put("2 CMC",   two);
                decklist.put("3 CMC",   three);
                decklist.put("4 CMC",   four);
                decklist.put("5 CMC",   five);
                decklist.put("6 CMC",   six);
                decklist.put("Rock",    rock);
                decklist.put("Sol Ring",1);
                decklist.put("Draw",    draw);
                decklist.put("Land",    land);

                double totalMana = 0;
                for (int s = 0; s < numSims; s++) totalMana += runOneSim()[0];
                double avg = Math.round(totalMana / numSims * 10000.0) / 10000.0;

                int    newN = prevN + numSims;
                double newE = Math.round(((prevE * prevN) + (avg * numSims)) / newN * 10000.0) / 10000.0;
                numberSims.put(key, newN);
                estimation.put(key, newE);

                boolean samePrev = one==bestOne && two==bestTwo && three==bestThree && four==bestFour
                                && five==bestFive && six==bestSix && rock==bestRock;
                if (newE >= bestManaSpent) {
                    String tag = samePrev ? "Update!" : newE >= previousBestManaSpent ? "Improv!" : "-------";
                    System.out.printf("---%s Deck %d,%d,%d,%d,%d,%d,%d,%d  prev=%.4f/%d  now=%.4f/%d%n",
                        tag, one, two, three, four, five, six, rock, land, prevE, prevN, newE, newN);
                    bestManaSpent = newE;
                    newBestOne=one; newBestTwo=two; newBestThree=three; newBestFour=four;
                    newBestFive=five; newBestSix=six; newBestRock=rock; newBestDraw=draw; newBestLand=land;
                    simsForBest = newN;
                } else if (newE < previousBestManaSpent && newE > 0.998 * bestManaSpent) {
                    String tag = samePrev ? "Update!" : "Close! ";
                    System.out.printf("---%s Deck %d,%d,%d,%d,%d,%d,%d,%d  prev=%.4f/%d  now=%.4f/%d%n",
                        tag, one, two, three, four, five, six, rock, land, prevE, prevN, newE, newN);
                }
            }

            boolean prevStillBest = newBestOne==bestOne && newBestTwo==bestTwo && newBestThree==bestThree
                                 && newBestFour==bestFour && newBestFive==bestFive && newBestSix==bestSix
                                 && newBestRock==bestRock && newBestDraw==bestDraw;
            previousBestManaSpent = bestManaSpent;
            continueSearching = !(prevStillBest && simsForBest > 200_000);

            bestOne=newBestOne; bestTwo=newBestTwo; bestThree=newBestThree; bestFour=newBestFour;
            bestFive=newBestFive; bestSix=newBestSix; bestRock=newBestRock; bestDraw=newBestDraw; bestLand=newBestLand;

            // Check if any previously seen deck beats current best
            for (Map.Entry<String, Double> e : estimation.entrySet()) {
                if (e.getValue() >= bestManaSpent && numberSims.get(e.getKey()) >= previousSimsForBest / 2) {
                    bestManaSpent = e.getValue();
                    String[] parts = e.getKey().split(",");
                    bestOne=Integer.parseInt(parts[0]); bestTwo=Integer.parseInt(parts[1]);
                    bestThree=Integer.parseInt(parts[2]); bestFour=Integer.parseInt(parts[3]);
                    bestFive=Integer.parseInt(parts[4]); bestSix=Integer.parseInt(parts[5]);
                    bestRock=Integer.parseInt(parts[6]); bestDraw=Integer.parseInt(parts[7]); bestLand=Integer.parseInt(parts[8]);
                    simsForBest = numberSims.get(e.getKey());
                }
            }

            numSims += 1000;
            previousSimsForBest = simsForBest;
            System.out.printf("====> Deck: %d one, %d two, %d three, %d four, %d five, %d six, %d rock, 1 Sol Ring, %d lands ==> %.4f%n",
                bestOne, bestTwo, bestThree, bestFour, bestFive, bestSix, bestRock, bestLand, bestManaSpent);
        }
    }
}
