package dev.donutplus.screen;

import dev.donutplus.Auction;
import dev.donutplus.Util;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class AuctionScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget item, secs, start;
    private static String lastItem = "", lastSecs = "120", lastStart = "100k";

    public AuctionScreen(Screen parent) { super(Text.literal("Auctions")); this.parent = parent; }

    @Override
    protected void init() {
        int x = width / 2 - 100;
        item = new TextFieldWidget(textRenderer, x, 40, 200, 18, Text.literal("Item"));
        item.setPlaceholder(Text.literal("Item name")); item.setText(lastItem); addDrawableChild(item);
        secs = new TextFieldWidget(textRenderer, x, 64, 95, 18, Text.literal("Seconds"));
        secs.setText(lastSecs); addDrawableChild(secs);
        start = new TextFieldWidget(textRenderer, x + 105, 64, 95, 18, Text.literal("Start bid"));
        start.setText(lastStart); addDrawableChild(start);
        addDrawableChild(ButtonWidget.builder(Text.literal("Start"), b -> {
            lastItem = item.getText(); lastSecs = secs.getText(); lastStart = start.getText();
            int s = (int) Util.parse(lastSecs);
            if (s > 0 && !lastItem.isBlank() && !Auction.active) Auction.start(lastItem, s, Util.parse(lastStart));
        }).dimensions(x, 88, 98, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("End now"), b -> Auction.stop()).dimensions(x + 102, 88, 98, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> client.setScreen(parent)).dimensions(x + 50, height - 28, 100, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        int cx = width / 2;
        ctx.drawCenteredTextWithShadow(textRenderer, "§6§lAuctions", cx, 12, 0xFFFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, "§7Bidders pay you with /pay. Fields: item, seconds, start bid (1k, 2.5m ok)", cx, 26, 0xFFFFFFFF);
        int y = 116;
        if (Auction.active)
            ctx.drawCenteredTextWithShadow(textRenderer, "§dLive: §f" + Auction.item + " §7- " + Util.clock(Auction.endTime - System.currentTimeMillis()) + " left", cx, y, 0xFFFFFFFF);
        y += 14;
        int n = 0;
        for (int i = Auction.bids.size() - 1; i >= 0 && n < 8; i--, n++) {
            Auction.Bid b = Auction.bids.get(i);
            ctx.drawCenteredTextWithShadow(textRenderer, (n == 0 ? "§a★ " : "§7") + b.player() + " §f" + Util.fmt(b.amount()), cx, y, 0xFFFFFFFF);
            y += 10;
        }
        y += 8;
        ctx.drawCenteredTextWithShadow(textRenderer, "§nPast auctions", cx, y, 0xFFFFFFFF); y += 11;
        for (int i = Auction.history.size() - 1; i >= 0 && y < height - 40; i--) {
            ctx.drawCenteredTextWithShadow(textRenderer, "§8" + Auction.history.get(i), cx, y, 0xFFFFFFFF);
            y += 10;
        }
    }
}
