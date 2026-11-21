package dev.donutplus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Reads chat lines and feeds ledger, auction, streak and popups. */
public class Tracker {
    public record Pay(String type, String player, double amount) {}

    /** Pure parse, no side effects. type = IN / OUT / SELL. */
    public static Pay parse(String line) {
        Config c = Config.I;
        Matcher m = match(c.recvRegex, line);
        if (m != null) return new Pay("IN", m.group(1), Util.parse(m.group(2)));
        m = match(c.sentRegex, line);
        if (m != null) return new Pay("OUT", m.group(1), Util.parse(m.group(2)));
        m = match(c.sellRegex, line);
        if (m != null) return new Pay("SELL", "shop", Util.parse(m.group(1)));
        return null;
    }

    public static void handle(String line) {
        Config c = Config.I;
        Pay p = parse(line);
        if (p != null) {
            Ledger.add(p.type(), p.player(), p.amount());
            if (p.type().equals("IN")) {
                Auction.onPayment(p.player(), p.amount());
                if (c.popup && p.amount() >= c.popupMin) {
                    Popup.show("Payment received", Util.fmt(p.amount()) + " from " + p.player(), Util.color(c.colHuge, 0xFF55FF), c.popupSeconds);
                    if (c.popupSound) {
                        var mc = net.minecraft.client.MinecraftClient.getInstance();
                        if (mc.player != null) mc.player.playSound(net.minecraft.sound.SoundEvents.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                    }
                }
            }
            return;
        }
        Matcher m = match(c.balanceRegex, line);
        if (m != null) { Ledger.balance = Util.parse(m.group(1)); return; }
        if (match(c.winRegex, line) != null) Streak.win();
        else if (match(c.lossRegex, line) != null) Streak.loss();
    }

    private static Matcher match(String regex, String line) {
        Pattern p = Util.pattern(regex);
        if (p == null) return null;
        Matcher m = p.matcher(line);
        return m.find() ? m : null;
    }
}
