package dev.donutplus.screen;

import dev.donutplus.Config;
import dev.donutplus.Util;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;

public class ToolsScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget kw, max, guard, pay;
    private final TextFieldWidget[] cmds = new TextFieldWidget[4];

    public ToolsScreen(Screen parent) { super(Text.literal("Tools")); this.parent = parent; }

    private TextFieldWidget field(int x, int y, int w, String text) {
        TextFieldWidget f = new TextFieldWidget(textRenderer, x, y, w, 18, Text.empty());
        f.setMaxLength(256); f.setText(text); addDrawableChild(f); return f;
    }

    @Override
    protected void init() {
        Config c = Config.I;
        int x = width / 2 - 100;
        kw = field(x, 36, 120, c.watchKeyword);
        max = field(x + 125, 36, 75, c.watchMaxUnit == 0 ? "" : String.valueOf((long) c.watchMaxUnit));
        guard = field(x, 78, 200, String.join(",", c.guardNames));
        pay = field(x, 110, 200, String.valueOf((long) c.payConfirmAbove));
        for (int i = 0; i < 4; i++) cmds[i] = field(x + (i % 2) * 102, 146 + (i / 2) * 22, 98, c.hotkeyCommands[i]);
        addDrawableChild(ButtonWidget.builder(Text.literal("Save & Back"), b -> { save(); client.setScreen(parent); })
                .dimensions(x + 50, height - 28, 100, 20).build());
    }

    private void save() {
        Config c = Config.I;
        c.watchKeyword = kw.getText().trim();
        c.watchMaxUnit = Util.parse(max.getText());
        c.guardNames = new ArrayList<>(Arrays.stream(guard.getText().split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList());
        double p = Util.parse(pay.getText());
        if (p > 0) c.payConfirmAbove = p;
        for (int i = 0; i < 4; i++) c.hotkeyCommands[i] = cmds[i].getText().trim();
        Config.save();
    }

    @Override
    public void close() { save(); super.close(); }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        int x = width / 2 - 100;
        ctx.drawCenteredTextWithShadow(textRenderer, "§6§lTools", width / 2, 10, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, "§7Price Watch: item keyword / max price per item (enable in Features)", x, 25, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, "§7Pay guard: always hold /pay /tpa for these names (comma separated)", x, 67, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, "§7Hold /pay at or above this amount", x, 99, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, "§7Command hotkeys 1-4 (bind keys in Controls) - one command per press", x, 135, 0xFFFFFFFF);
    }
}
