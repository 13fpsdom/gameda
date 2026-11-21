package dev.donutplus;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class Popup {
    private static String title = "", sub = "";
    private static int color;
    private static long start = -1, dur;

    public static void show(String t, String s, int rgb, int seconds) {
        title = t; sub = s; color = rgb; start = System.currentTimeMillis(); dur = Math.max(1, seconds) * 1000L;
    }

    public static void render(DrawContext ctx) {
        if (start < 0) return;
        long el = System.currentTimeMillis() - start;
        if (el > dur) { start = -1; return; }
        TextRenderer tr = MinecraftClient.getInstance().textRenderer;
        int w = Math.max(tr.getWidth(title), tr.getWidth(sub)) + 28, h = 34;
        float k = Math.min(Math.min(1f, el / 250f), Math.min(1f, (dur - el) / 250f));
        k = k * k * (3 - 2 * k);
        int cx = ctx.getScaledWindowWidth() / 2, x = cx - w / 2, y = (int) (-h + k * (h + 10));
        ctx.fill(x, y, x + w, y + h, 0xE0101018);
        ctx.fill(x, y, x + w, y + 2, 0xFF000000 | color);
        ctx.drawCenteredTextWithShadow(tr, title, cx, y + 8, 0xFF000000 | color);
        ctx.drawCenteredTextWithShadow(tr, sub, cx, y + 20, 0xFFFFFFFF);
    }
}
