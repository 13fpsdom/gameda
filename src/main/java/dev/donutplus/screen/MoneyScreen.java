package dev.donutplus.screen;

import dev.donutplus.Config;
import dev.donutplus.Ledger;
import dev.donutplus.PayFilter;
import dev.donutplus.Streak;
import dev.donutplus.Util;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MoneyScreen extends Screen {
    private final Screen parent;
    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("dd/MM HH:mm").withZone(ZoneId.systemDefault());

    public MoneyScreen(Screen parent) { super(Text.literal("Money Tracker")); this.parent = parent; }

    @Override
    protected void init() {
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> client.setScreen(parent))
                .dimensions(width / 2 - 105, height - 28, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Clear history"), b -> { Ledger.entries.clear(); Ledger.save(); })
                .dimensions(width / 2 + 5, height - 28, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Win +1"), b -> Streak.win()).dimensions(width / 2 - 105, height - 52, 66, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Loss"), b -> Streak.loss()).dimensions(width / 2 - 33, height - 52, 66, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset streak"), b -> Streak.reset()).dimensions(width / 2 + 39, height - 52, 66, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        int cx = width / 2, y = 14;
        ctx.drawCenteredTextWithShadow(textRenderer, "§6§lMoney Tracker", cx, y, 0xFFFFFFFF); y += 16;
        ctx.drawCenteredTextWithShadow(textRenderer, "§fBalance: " + (Ledger.balance < 0 ? "§7unknown - run /bal" : "§a" + Util.fmt(Ledger.balance)), cx, y, 0xFFFFFFFF); y += 14;

        long now = System.currentTimeMillis();
        long[] since = {Ledger.SESSION_START, now - 86_400_000L, now - 7 * 86_400_000L};
        String[] names = {"Session", "24 hours", "7 days"};
        for (int i = 0; i < 3; i++) {
            double in = Ledger.sumAll(since[i], true), out = Ledger.sumAll(since[i], false);
            ctx.drawCenteredTextWithShadow(textRenderer, "§7" + names[i] + ": §a+" + Util.fmt(in) + " §c-" + Util.fmt(out)
                    + " §fnet " + Util.fmt(in - out) + " §8(sales " + Util.fmt(Ledger.sum("SELL", since[i])) + ")", cx, y, 0xFFFFFFFF);
            y += 11;
        }
        double hrs = Math.max(1 / 60.0, (now - Ledger.SESSION_START) / 3_600_000.0);
        ctx.drawCenteredTextWithShadow(textRenderer, "§7Sell income/hour: §e" + Util.fmt(Ledger.sum("SELL", Ledger.SESSION_START) / hrs), cx, y, 0xFFFFFFFF);
        y += 11;
        Config c = Config.I;
        ctx.drawCenteredTextWithShadow(textRenderer, "§bWin streak: §f" + c.streak + " §7(best " + c.bestStreak + ", " + c.wins + "W/" + c.losses + "L)  §8hidden payments: " + PayFilter.hidden, cx, y, 0xFFFFFFFF);
        y += 16;
        ctx.drawCenteredTextWithShadow(textRenderer, "§nRecent transactions", cx, y, 0xFFFFFFFF); y += 12;
        List<Ledger.Entry> e = Ledger.entries;
        int shown = 0;
        for (int i = e.size() - 1; i >= 0 && shown < (height - y - 62) / 10; i--, shown++) {
            Ledger.Entry en = e.get(i);
            String col = en.type.equals("OUT") ? "§c-" : "§a+";
            ctx.drawCenteredTextWithShadow(textRenderer, "§8" + F.format(Instant.ofEpochMilli(en.time)) + " §7" + en.type + " " + col + Util.fmt(en.amount) + " §f" + en.player, cx, y, 0xFFFFFFFF);
            y += 10;
        }
    }
}
