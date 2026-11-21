package dev.donutplus.screen;

import dev.donutplus.FilePicker;
import dev.donutplus.TextureManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.text.Text;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PacksScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget path;
    private static String status = "";
    private int page = 0;
    private final List<String[]> rows = new ArrayList<>(); // name, y

    public PacksScreen(Screen parent) { super(Text.literal("Texture Packs")); this.parent = parent; }

    @Override
    protected void init() {
        int cx = width / 2;
        path = new TextFieldWidget(textRenderer, cx - 150, 40, 190, 18, Text.literal("Path"));
        path.setMaxLength(512); path.setPlaceholder(Text.literal("Path to a .zip resource pack")); addDrawableChild(path);
        addDrawableChild(ButtonWidget.builder(Text.literal("Browse"), b -> {
            String p = FilePicker.pick("Select a resource pack (.zip)");
            if (p != null) path.setText(p); else status = "No file chosen (or file dialog unavailable) - paste the path instead.";
        }).dimensions(cx + 45, 40, 50, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Import"), b ->
                status = TextureManager.importPack(Path.of(path.getText().trim().replace("\"", "")))
        ).dimensions(cx + 98, 40, 52, 18).build());

        ResourcePackManager rpm = client.getResourcePackManager();
        rpm.scanPacks();
        List<ResourcePackProfile> list = new ArrayList<>();
     for (ResourcePackProfile p : rpm.getProfiles()) {
    String pid = p.getId();
    if (!pid.equals("vanilla") && !pid.startsWith("fabric")) list.add(p);
}
        list.sort(Comparator.comparing(p -> p.getDisplayName().getString().toLowerCase()));
        int per = Math.max(3, (height - 130) / 22);
        int pages = Math.max(1, (list.size() + per - 1) / per);
        page = Math.max(0, Math.min(page, pages - 1));
        rows.clear();
        for (int i = page * per; i < Math.min(list.size(), page * per + per); i++) {
            ResourcePackProfile p = list.get(i);
            int y = 78 + (i - page * per) * 22;
            rows.add(new String[]{p.getDisplayName().getString(), String.valueOf(y)});
            boolean on = rpm.getEnabledIds().contains(p.getId());
            addDrawableChild(ButtonWidget.builder(Text.literal(on ? "§aON" : "§cOFF"), b -> {
                if (rpm.getEnabledIds().contains(p.getId())) rpm.disable(p.getId()); else rpm.enable(p.getId());
                client.options.refreshResourcePacks(rpm);
                clearAndInit();
            }).dimensions(cx + 100, y, 50, 18).build());
        }
        int by = height - 26;
        addDrawableChild(ButtonWidget.builder(Text.literal("< Prev"), b -> { page--; clearAndInit(); }).dimensions(cx - 160, by, 70, 20).build()).active = page > 0;
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> close()).dimensions(cx - 50, by, 100, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Next >"), b -> { page++; clearAndInit(); }).dimensions(cx + 90, by, 70, 20).build()).active = page < pages - 1;
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        int cx = width / 2;
        ctx.drawCenteredTextWithShadow(textRenderer, "§6§lTexture Packs", cx, 12, 0xFFFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, "§7Import a pack, then switch installed packs on/off", cx, 26, 0xFFFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, "§e" + status, cx, 62, 0xFFFFFFFF);
        for (String[] r : rows) {
            String n = r[0];
            if (textRenderer.getWidth(n) > 240) n = textRenderer.trimToWidth(n, 235) + "...";
            ctx.drawTextWithShadow(textRenderer, n, cx - 150, Integer.parseInt(r[1]) + 5, 0xFFFFFFFF);
        }
    }

    @Override
    public void close() { client.setScreen(parent); }
}
