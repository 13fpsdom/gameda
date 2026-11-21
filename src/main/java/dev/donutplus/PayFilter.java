package dev.donutplus;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

/** Hides tiny and spammed incoming payments from chat (they are still counted in the ledger). */
public class PayFilter {
    public static int hidden = 0;
    private static final Map<String, ArrayDeque<Long>> recent = new HashMap<>();

    public static boolean hide(Tracker.Pay p) {
        Config c = Config.I;
        if (p == null || !p.type().equals("IN")) return false;
        if (c.filterSmall && p.amount() < c.smallBelow) { hidden++; return true; }
        if (c.filterSpam) {
            long now = System.currentTimeMillis(), win = c.spamWindowSec * 1000L;
            ArrayDeque<Long> q = recent.computeIfAbsent(p.player().toLowerCase(), k -> new ArrayDeque<>());
            while (!q.isEmpty() && now - q.peekFirst() > win) q.pollFirst();
            q.addLast(now);
            if (q.size() > c.spamMax) { hidden++; return true; }
        }
        return false;
    }
}
