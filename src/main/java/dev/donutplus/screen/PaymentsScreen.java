package dev.donutplus.screen;

import dev.donutplus.Config;
import dev.donutplus.PayFilter;
import dev.donutplus.Util;
import net.minecraft.client.gui.screen.Screen;

public class PaymentsScreen extends FormScreen {
    public PaymentsScreen(Screen parent) { super("Payments", parent); }

    @Override
    protected void build() {
        Config c = Config.I;
        tog("Colour payment chat", () -> c.payColors, v -> c.payColors = v);
        tog("Compact payment format", () -> c.compactPay, v -> c.compactPay = v);
        text("Medium tier from $", () -> Util.num(c.tierMedium), s -> c.tierMedium = Util.parse(s));
        text("Big tier from $", () -> Util.num(c.tierBig), s -> c.tierBig = Util.parse(s));
        text("Huge tier from $", () -> Util.num(c.tierHuge), s -> c.tierHuge = Util.parse(s));
        text("Colour: small (#RRGGBB)", () -> c.colSmall, s -> c.colSmall = s);
        text("Colour: medium", () -> c.colMedium, s -> c.colMedium = s);
        text("Colour: big", () -> c.colBig, s -> c.colBig = s);
        text("Colour: huge", () -> c.colHuge, s -> c.colHuge = s);
        text("Colour: payments you send", () -> c.colOut, s -> c.colOut = s);
        tog("Popup for big payments", () -> c.popup, v -> c.popup = v);
        text("Popup from $", () -> Util.num(c.popupMin), s -> c.popupMin = Util.parse(s));
        text("Popup seconds", () -> String.valueOf(c.popupSeconds), s -> c.popupSeconds = (int) Util.parse(s));
        tog("Popup sound", () -> c.popupSound, v -> c.popupSound = v);
        tog("Hide small payments", () -> c.filterSmall, v -> c.filterSmall = v);
        text("Hide payments below $", () -> Util.num(c.smallBelow), s -> c.smallBelow = Util.parse(s));
        tog("Hide spam payments", () -> c.filterSpam, v -> c.filterSpam = v);
        text("Spam: max payments per sender", () -> String.valueOf(c.spamMax), s -> c.spamMax = (int) Util.parse(s));
        text("Spam: window (seconds)", () -> String.valueOf(c.spamWindowSec), s -> c.spamWindowSec = (int) Util.parse(s));
        tog("/pay confirmation screen", () -> c.payGuard, v -> c.payGuard = v);
        text("Confirm /pay from $", () -> Util.num(c.payConfirmAbove), s -> c.payConfirmAbove = Util.parse(s));
        text("Confirm button delay (ms)", () -> String.valueOf(c.confirmDelayMs), s -> c.confirmDelayMs = (int) Util.parse(s));
    }

    @Override
    protected void extra() {
        // hidden-message counter is shown in the Money Tracker screen
    }
}
