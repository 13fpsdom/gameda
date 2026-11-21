package dev.donutplus.screen;

import dev.donutplus.Config;
import dev.donutplus.Util;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class SkyScreen extends FormScreen {
    private static final String[] NAMES = {"Pink", "Purple", "Cyan", "Orange", "Red", "Green"};
    private static final String[] HEX = {"#FF69B4", "#9B59FF", "#33E0FF", "#FF8A2B", "#FF3B3B", "#3BFF7A"};

    public SkyScreen(Screen parent) { super("Sky", parent); }

    @Override
    protected void build() {
        Config c = Config.I;
        cyc("Sky mode", new String[]{"Off (vanilla)", "Solid colour", "Rainbow"}, () -> c.skyMode, v -> c.skyMode = v);
        text("Sky colour (#RRGGBB)", () -> c.skyColor, s -> c.skyColor = s);
        text("Rainbow speed", () -> Util.num(c.rainbowSpeed), s -> c.rainbowSpeed = Util.parse(s));
        tog("Tint horizon fog too", () -> c.skyFog, v -> c.skyFog = v);
        text("Fog colour", () -> c.fogColor, s -> c.fogColor = s);
        tog("Custom cloud colour", () -> c.skyClouds, v -> c.skyClouds = v);
        text("Cloud colour", () -> c.cloudColor, s -> c.cloudColor = s);
    }

    @Override
    protected void extra() {
        int x = width / 2 - 165, y = height - 52;
        for (int i = 0; i < NAMES.length; i++) {
            final int k = i;
            addDrawableChild(ButtonWidget.builder(Text.literal(NAMES[i]), b -> {
                Config.I.skyColor = HEX[k]; Config.I.fogColor = HEX[k];
                if (Config.I.skyMode == 0) Config.I.skyMode = 1;
                clearAndInit();
            }).dimensions(x + i * 56, y, 54, 18).build());
        }
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        int c = 0xFF000000 | Util.color(Config.I.skyColor, 0xFF69B4);
        ctx.fill(width / 2 + 165, 34, width / 2 + 185, 54, c);
    }
}
