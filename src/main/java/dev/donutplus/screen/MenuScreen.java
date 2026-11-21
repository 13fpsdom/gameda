package dev.donutplus.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class MenuScreen extends Screen {
    public MenuScreen() { super(Text.literal("DonutPlus")); }

    private int top() { return Math.max(40, height / 2 - 84); }

    private void tile(int col, int row, String label, Runnable r) {
        int w = 150, h = 22, gap = 6;
        int x = width / 2 - w - gap / 2 + col * (w + gap);
        int y = top() + row * (h + gap);
        addDrawableChild(ButtonWidget.builder(Text.literal(label), b -> r.run()).dimensions(x, y, w, h).build());
    }

    @Override
    protected void init() {
        tile(0, 0, "Money Tracker", () -> client.setScreen(new MoneyScreen(this)));
        tile(1, 0, "Auctions", () -> client.setScreen(new AuctionScreen(this)));
        tile(0, 1, "Payments & Alerts", () -> client.setScreen(new PaymentsScreen(this)));
        tile(1, 1, "Tools & Price Watch", () -> client.setScreen(new ToolsScreen(this)));
        tile(0, 2, "Sky", () -> client.setScreen(new SkyScreen(this)));
        tile(1, 2, "Features & HUD", () -> client.setScreen(new SettingsScreen(this)));
        tile(0, 3, "Texture Packs", () -> client.setScreen(new PacksScreen(this)));
        tile(1, 3, "Texture Editor", () -> client.setScreen(new TextureEditScreen(this)));
        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), b -> close())
                .dimensions(width / 2 - 50, top() + 4 * 28 + 4, 100, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, "§6§lDonutPlus", width / 2, top() - 28, 0xFFFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, "§7Keybinds: Options > Controls > DonutPlus", width / 2, top() - 16, 0xFFFFFFFF);
    }
}
