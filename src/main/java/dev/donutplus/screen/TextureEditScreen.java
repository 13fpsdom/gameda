package dev.donutplus.screen;

import dev.donutplus.Config;
import dev.donutplus.FilePicker;
import dev.donutplus.TextureManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.nio.file.Path;

public class TextureEditScreen extends Screen {
    private static final String[] NAMES = {"Totem of Undying", "End Crystal", "Obsidian", "Diamond Sword", "Netherite Sword",
            "Golden Apple", "Ender Pearl", "Elytra", "Spawner", "Sun", "Moon phases", "Custom id"};
    private static final String[] IDS = {"item/totem_of_undying", "entity/end_crystal/end_crystal", "block/obsidian", "item/diamond_sword",
            "item/netherite_sword", "item/golden_apple", "item/ender_pearl", "item/elytra", "block/spawner", "environment/sun",
            "environment/moon_phases", ""};
    private static int preset = 0;
    private static String status = "";
    private final Screen parent;
    private TextFieldWidget id, png;

    public TextureEditScreen(Screen parent) { super(Text.literal("Texture Editor")); this.parent = parent; }

    @Override
    protected void init() {
        int cx = width / 2, x = cx - 150;
        addDrawableChild(ButtonWidget.builder(Text.literal("Texture: " + NAMES[preset]), b -> {
            preset = (preset + 1) % NAMES.length;
            b.setMessage(Text.literal("Texture: " + NAMES[preset]));
            if (!IDS[preset].isEmpty()) id.setText(IDS[preset]);
        }).dimensions(x, 40, 300, 20).build());
        id = new TextFieldWidget(textRenderer, x, 76, 300, 18, Text.literal("id"));
        id.setMaxLength(200); id.setText(Config.I.lastTextureId); addDrawableChild(id);
        png = new TextFieldWidget(textRenderer, x, 112, 230, 18, Text.literal("png"));
        png.setMaxLength(512); png.setPlaceholder(Text.literal("Path to your replacement .png")); addDrawableChild(png);
        addDrawableChild(ButtonWidget.builder(Text.literal("Browse"), b -> {
            String p = FilePicker.pick("Select a PNG image");
            if (p != null) png.setText(p); else status = "No file chosen (or dialog unavailable) - paste the path.";
        }).dimensions(x + 235, 112, 65, 18).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Apply"), b -> {
            Config.I.lastTextureId = id.getText().trim(); Config.save();
            status = TextureManager.apply(id.getText(), Path.of(png.getText().trim().replace("\"", "")));
        }).dimensions(x, 142, 97, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset this"), b -> status = TextureManager.reset(id.getText()))
                .dimensions(x + 101, 142, 97, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Reset all"), b -> status = TextureManager.resetAll())
                .dimensions(x + 202, 142, 98, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Back"), b -> close()).dimensions(cx - 50, height - 26, 100, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        super.render(ctx, mx, my, delta);
        int cx = width / 2, x = cx - 150;
        ctx.drawCenteredTextWithShadow(textRenderer, "§6§lTexture Editor", cx, 12, 0xFFFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, "§7Replace any texture with your own PNG (same size as the original)", cx, 26, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, "§7Texture id (path under assets/minecraft/textures, no .png)", x, 65, 0xFFFFFFFF);
        ctx.drawTextWithShadow(textRenderer, "§7Replacement image", x, 101, 0xFFFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, "§e" + status, cx, 174, 0xFFFFFFFF);
        ctx.drawCenteredTextWithShadow(textRenderer, "§8Overrides live in resourcepacks/" + TextureManager.PACK, cx, 188, 0xFFFFFFFF);
    }

    @Override
    public void close() { client.setScreen(parent); }
}
