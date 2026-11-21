package dev.donutplus.screen;

import dev.donutplus.Config;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

/** Simple paged settings form: text fields, on/off toggles and cycle buttons. Saves config automatically. */
public abstract class FormScreen extends Screen {
    protected final Screen parent;
    protected final String heading;
    protected int page = 0;
    private final List<E> entries = new ArrayList<>();
    private final List<int[]> rowY = new ArrayList<>();

    protected static class E {
        int kind; String label;
        Supplier<String> get; Consumer<String> setS;
        BooleanSupplier getB; Consumer<Boolean> setB;
        String[] opts; IntSupplier getI; IntConsumer setI;
    }

    protected FormScreen(String heading, Screen parent) {
        super(Text.literal(heading));
        this.heading = heading; this.parent = parent;
    }

    protected abstract void build();
    protected void extra() {}

    protected void text(String l, Supplier<String> g, Consumer<String> s) { E e = new E(); e.kind = 0; e.label = l; e.get = g; e.setS = s; entries.add(e); }
    protected void tog(String l, BooleanSupplier g, Consumer<Boolean> s) { E e = new E(); e.kind = 1; e.label = l; e.getB = g; e.setB = s; entries.add(e); }
    protected void cyc(String l, String[] o, IntSupplier g, IntConsumer s) { E e = new E(); e.kind = 2; e.label = l; e.opts = o; e.getI = g; e.setI = s; entries.add(e); }

    @Override
    protected void init() {
        entries.clear(); build();
        int cx = width / 2;
        int perPage = Math.max(3, (height - 96) / 22);
        int pages = Math.max(1, (entries.size() + perPage - 1) / perPage);
        page = Math.max(0, Math.min(page, pages - 1));
        int start = page * perPage, end = Math.min(entries.size(), start + perPage);
        rowY.clear();
        for (int i = start; i < end; i++) {
            E e = entries.get(i);
            int y = 34 + (i - start) * 22;
            rowY.add(new int[]{i, y});
            int x = cx + 10, w = 150;
            if (e.kind == 0) {
                TextFieldWidget f = new TextFieldWidget(textRenderer, x, y, w, 18, Text.literal(e.label));
                f.setMaxLength(256);
                f.setText(e.get.get());
                f.setChangedListener(e.setS);
                addDrawableChild(f);
            } else if (e.kind == 1) {
                ButtonWidget[] b = new ButtonWidget[1];
                b[0] = ButtonWidget.builder(Text.literal(e.getB.getAsBoolean() ? "§aON" : "§cOFF"), btn -> {
                    boolean v = !e.getB.getAsBoolean();
                    e.setB.accept(v);
                    btn.setMessage(Text.literal(v ? "§aON" : "§cOFF"));
                }).dimensions(x, y, w, 18).build();
                addDrawableChild(b[0]);
            } else {
                addDrawableChild(ButtonWidget.builder(Text.literal(e.opts[e.getI.getAsInt() % e.opts.length]), btn -> {
                    int v = (e.getI.getAsInt() + 1) % e.opts.length;
                    e.setI.accept(v);
                    btn.setMessage(Text.literal(e.opts[v]));
                }).dimensions(x, y, w, 18).build());
            }
        }
        int by = height - 26;
        addDrawableChild(ButtonWidget.builder(Text.literal("< Prev"), b -> { page--; clearAndInit(); }).dimensions(cx - 160, by, 70, 20).build()).active = page > 0;
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> close()).dimensions(cx - 50, by, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Next >"), b -> { page++; clearAndInit(); }).dimensions(cx + 90, by, 70, 20).build()).active = page < pages - 1;
        extra();
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        ctx.drawCenteredTextWithShadow(textRenderer, "§6§l" + heading, width / 2, 12, 0xFFFFFFFF);
        for (int[] r : rowY)
            ctx.drawTextWithShadow(textRenderer, "§7" + entries.get(r[0]).label, width / 2 - 165, r[1] + 5, 0xFFFFFFFF);
    }

    @Override
    public void removed() { Config.save(); }

    @Override
    public void close() { client.setScreen(parent); }
}
