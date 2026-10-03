package com.example.mtg_deckbuilder.views.api;

/**
 * Commander-style role counts derived from deck card oracle text.
 */
public record DeckRoleStats(int ramp, int draw, int removal, int boardWipes) {

    public static final DeckRoleStats EMPTY = new DeckRoleStats(0, 0, 0, 0);

    public RoleHint rampHint() {
        return hint(ramp, 8, 12);
    }

    public RoleHint drawHint() {
        return hint(draw, 8, 10);
    }

    public RoleHint removalHint() {
        return hint(removal, 8, 10);
    }

    public RoleHint boardWipesHint() {
        return hint(boardWipes, 2, 4);
    }

    private static RoleHint hint(int count, int low, int high) {
        if (count < low) {
            int delta = low - count;
            return new RoleHint(delta == 1 ? "−1 low" : "−" + delta + " low", "muted");
        }
        if (count > high) {
            int delta = count - high;
            return new RoleHint("+" + delta + " high", "high");
        }
        return new RoleHint("Balanced", "balanced");
    }

    public record RoleHint(String label, String tone) {
    }
}
