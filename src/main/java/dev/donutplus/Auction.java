package dev.donutplus;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/** Streamer-style auction: players bid by sending you /pay. Highest payment wins. */
public class Auction {
    public record Bid(String player, double amount, long time) {}

    public static boolean active = false;
    public static String item = "";
    public static double startBid = 0;
    public static long endTime = 0;
    public static final List<Bid> bids = new ArrayList<>();
    public static final List<String> history = new ArrayList<>();

    public static void start(String item, int seconds, double startBid) {
        Auction.item = item; Auction.startBid = startBid;
        endTime = System.currentTimeMillis() + seconds * 1000L;
        bids.clear(); active = true;
        say("§6[Auction] §fStarted: §e" + item + " §fstarting at " + Util.fmt(startBid));
    }

    public static Bid top() { return bids.isEmpty() ? null : bids.get(bids.size() - 1); }

    public static void onPayment(String who, double amt) {
        if (!active || amt < startBid) return;
        Bid t = top();
        if (t != null && amt <= t.amount()) return;
        bids.add(new Bid(who, amt, System.currentTimeMillis()));
        say("§6[Auction] §fNew top bid: §a" + Util.fmt(amt) + " §fby §e" + who);
    }

    public static void stop() {
        if (!active) return;
        active = false;
        Bid t = top();
        String res = t == null ? "no bids" : t.player() + " won with " + Util.fmt(t.amount());
        history.add(item + ": " + res);
        say("§6[Auction] §fEnded - §e" + res);
    }

    public static void tick() {
        if (active && System.currentTimeMillis() >= endTime) stop();
    }

    private static void say(String s) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) mc.player.sendMessage(Text.literal(s), false);
    }
}
