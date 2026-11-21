package dev.donutplus.screen;

import dev.donutplus.Config;
import dev.donutplus.DonutPlusClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.List;

/** Shown instead of sending a big /pay (or a /tpa to a watched player). */
public class PayConfirmScreen extends Screen {
    private final String command;
    private final List<String> lines;
    private final long opened = System.currentTimeMillis();
    private ButtonWidget confirm;

    public PayConfirmScreen(String command, List<String> lines) {
        super(Text.literal("Confirm"));
        this.command = command; this.lines = lines;
    }

    @Override
    protected void init() {
        int cx = width / 2, y = height / 2 + 20;
        confirm = addDrawableChild(ButtonWidget.builder(Text.literal("Confirm"), b -> {
            client.setScreen(null);
            DonutPlusClient.sendBypassed(command);
        }).dimensions(cx - 105, y, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), b -> close()).dimensions(cx + 5, y, 100, 20).build());
    }

    @Override
    public void tick() {
        long left = Config.I.confirmDelayMs - (System.currentTimeMillis() - opened);
        confirm.active = left <= 0;
        confirm.setMessage(Text.literal(left > 0 ? "Confirm (" + (left / 100 / 10.0) + "s)" : "§aConfirm"));
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        int cx = width / 2, y = height / 2 - 40;
        ctx.drawCenteredTextWithShadow(textRenderer, "§c§lConfirm payment", cx, y - 16, 0xFFFFFFFF);
        for (String l : lines) { ctx.drawCenteredTextWithShadow(textRenderer, l, cx, y, 0xFFFFFFFF); y += 12; }
        ctx.drawCenteredTextWithShadow(textRenderer, "§8/" + command, cx, height / 2 + 4, 0xFFFFFFFF);
    }

    @Override
    public boolean shouldPause() { return false; }
}
