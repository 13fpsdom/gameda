package dev.donutplus.screen;

import dev.donutplus.Config;
import dev.donutplus.Util;
import net.minecraft.client.gui.screen.Screen;

public class SettingsScreen extends FormScreen {
    public SettingsScreen(Screen parent) { super("Features & HUD", parent); }

    @Override
    protected void build() {
        Config c = Config.I;
        tog("HUD enabled", () -> c.hud, v -> c.hud = v);
        tog("Show balance", () -> c.hudBalance, v -> c.hudBalance = v);
        tog("Show profit of the day", () -> c.hudDaily, v -> c.hudDaily = v);
        text("Daily profit goal (0 = off)", () -> Util.num(c.dailyGoal), s -> c.dailyGoal = Util.parse(s));
        tog("Show session income", () -> c.hudSession, v -> c.hudSession = v);
        tog("Show win streak", () -> c.hudStreak, v -> c.hudStreak = v);
        tog("Show totem count", () -> c.hudTotems, v -> c.hudTotems = v);
        tog("Show coordinates", () -> c.hudCoords, v -> c.hudCoords = v);
        tog("Show auction timer", () -> c.hudAuction, v -> c.hudAuction = v);
        text("HUD X position", () -> String.valueOf(c.hudX), s -> c.hudX = (int) Util.parse(s));
        text("HUD Y position", () -> String.valueOf(c.hudY), s -> c.hudY = (int) Util.parse(s));
        text("HUD background (0-255)", () -> String.valueOf(c.hudBgAlpha), s -> c.hudBgAlpha = (int) Util.parse(s));
        tog("Armor durability warning", () -> c.armorWarn, v -> c.armorWarn = v);
        text("Armor warning at %", () -> String.valueOf(c.armorWarnPercent), s -> c.armorWarnPercent = (int) Util.parse(s));
        tog("Per-item price in tooltips", () -> c.unitPrice, v -> c.unitPrice = v);
        tog("Price watch (set in Tools)", () -> c.priceWatch, v -> c.priceWatch = v);
        tog("Ad blocker", () -> c.adBlock, v -> c.adBlock = v);
        text("Win regex", () -> c.winRegex, s -> c.winRegex = s);
        text("Loss regex", () -> c.lossRegex, s -> c.lossRegex = s);
        tog("Run on any server", () -> c.forceEnable, v -> c.forceEnable = v);
    }
}
