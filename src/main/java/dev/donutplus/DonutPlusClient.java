package dev.donutplus;

import dev.donutplus.screen.MenuScreen;
import dev.donutplus.screen.PayConfirmScreen;
import dev.donutplus.screen.SkyScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DonutPlusClient implements ClientModInitializer {
    static KeyBinding menuKey, hudKey, skyKey, streakKey;
    static final KeyBinding[] hotkeys = new KeyBinding[4];
    static boolean bypass = false;
    static Screen pending = null;
    static Object lastScreen = null;
    static final Set<String> alerted = new HashSet<>();
    static final Pattern PRICE = Pattern.compile("\\$([\\d.,]+[KMBkmb]?)");

    public static boolean active() {
        if (Config.I.forceEnable) return true;
        ServerInfo si = MinecraftClient.getInstance().getCurrentServerEntry();
        return si != null && si.address != null && si.address.toLowerCase().contains("donut");
    }

    /** Sends a command without it being intercepted by the pay guard again. */
    public static void sendBypassed(String cmd) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        bypass = true;
        try { mc.player.networkHandler.sendChatCommand(cmd); } finally { bypass = false; }
    }

    private static KeyBinding key(KeyBinding.Category cat, String name, int code) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding("key.donutplus." + name, InputUtil.Type.KEYSYM, code, cat));
    }

    @Override
    public void onInitializeClient() {
        Config.load();
        Ledger.load();

        KeyBinding.Category cat = KeyBinding.Category.create(Identifier.of("donutplus", "main"));
        menuKey = key(cat, "menu", GLFW.GLFW_KEY_RIGHT_SHIFT);
        hudKey = key(cat, "toggle_hud", GLFW.GLFW_KEY_UNKNOWN);
        skyKey = key(cat, "sky", GLFW.GLFW_KEY_UNKNOWN);
        streakKey = key(cat, "reset_streak", GLFW.GLFW_KEY_UNKNOWN);
        for (int i = 0; i < 4; i++) hotkeys[i] = key(cat, "command_" + (i + 1), GLFW.GLFW_KEY_UNKNOWN);

        HudElementRegistry.addLast(Identifier.of("donutplus", "hud"), (ctx, tick) -> Hud.render(ctx));
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);

        // 1) track money / streaks, filter payments, block ads
        ClientReceiveMessageEvents.ALLOW_GAME.register((message, overlay) -> {
            if (overlay || !active()) return true;
            String s = Formatting.strip(message.getString());
            if (s == null) return true;
            Tracker.handle(s);
            if (PayFilter.hide(Tracker.parse(s))) return false;
            if (Config.I.adBlock) {
                for (String r : Config.I.adPatterns) {
                    Pattern p = Util.pattern(r);
                    if (p != null && p.matcher(s).find()) return false;
                }
            }
            return true;
        });

        // 2) payment chat colours
        ClientReceiveMessageEvents.MODIFY_GAME.register((message, overlay) -> {
            Config c = Config.I;
            if (overlay || !active() || !c.payColors) return message;
            String s = Formatting.strip(message.getString());
            Tracker.Pay p = s == null ? null : Tracker.parse(s);
            if (p == null || p.type().equals("SELL")) return message;
            int rgb;
            boolean bold = false;
            if (p.type().equals("OUT")) rgb = Util.color(c.colOut, 0xFF5555);
            else if (p.amount() >= c.tierHuge) { rgb = Util.color(c.colHuge, 0xFF55FF); bold = true; }
            else if (p.amount() >= c.tierBig) rgb = Util.color(c.colBig, 0xFFAA00);
            else if (p.amount() >= c.tierMedium) rgb = Util.color(c.colMedium, 0x55FF55);
            else rgb = Util.color(c.colSmall, 0xAAAAAA);
            String text = s;
            if (c.compactPay)
                text = p.type().equals("IN") ? "+ " + Util.fmt(p.amount()) + "  from " + p.player() : "- " + Util.fmt(p.amount()) + "  to " + p.player();
            return Text.literal(text).setStyle(Style.EMPTY.withColor(rgb).withBold(bold));
        });

        // 3) /pay + /tpa confirmation screen
        ClientSendMessageEvents.ALLOW_COMMAND.register(cmd -> {
            Config c = Config.I;
            if (bypass || !active() || !c.payGuard) return true;
            String[] a = cmd.trim().split("\\s+");
            String base = a[0].toLowerCase();
            boolean pay = base.equals("pay");
            boolean tp = base.equals("tpa") || base.equals("tpahere") || base.equals("tpaccept");
            if (!pay && !tp) return true;
            boolean flagged = a.length > 1 && c.guardNames.stream().anyMatch(n -> n.equalsIgnoreCase(a[1]));
            double amt = pay && a.length > 2 ? Util.parse(a[2]) : 0;
            boolean big = pay && amt >= c.payConfirmAbove;
            if (!flagged && !big) return true;
            List<String> lines = new ArrayList<>();
            if (pay) {
                lines.add("Send " + Util.fmt(amt) + " to " + (a.length > 1 ? a[1] : "?") + "?");
                if (Ledger.balance > 0)
                    lines.add(String.format("That is %.0f%% of your balance (%s)", 100 * amt / Ledger.balance, Util.fmt(Ledger.balance)));
            } else lines.add("Run /" + cmd + " ?");
            if (flagged) lines.add("§c" + a[1] + " is on your watch list!");
            pending = new PayConfirmScreen(cmd, lines);
            return false;
        });

        // 4) per-item price in tooltips
        ItemTooltipCallback.EVENT.register((stack, ctxt, type, lines) -> {
            if (!Config.I.unitPrice || stack.getCount() < 2 || !active()) return;
            for (Text t : lines) {
                Matcher m = PRICE.matcher(t.getString());
                if (m.find()) {
                    double unit = Util.parse(m.group(1)) / stack.getCount();
                    lines.add(Text.literal("§7Per item: §a" + Util.fmt(unit)));
                    return;
                }
            }
        });
    }

    void tick(MinecraftClient mc) {
        if (mc.player == null) return;
        Auction.tick();

        // open the confirm screen once chat has closed itself
        if (pending != null && mc.currentScreen == null) { mc.setScreen(pending); pending = null; }

        while (menuKey.wasPressed()) mc.setScreen(new MenuScreen());
        while (skyKey.wasPressed()) mc.setScreen(new SkyScreen(null));
        while (hudKey.wasPressed()) { Config.I.hud = !Config.I.hud; Config.save(); }
        while (streakKey.wasPressed()) Streak.reset();

        for (int i = 0; i < 4; i++) {
            while (hotkeys[i].wasPressed()) {
                String cmd = Config.I.hotkeyCommands[i];
                if (cmd != null && !cmd.isBlank())
                    mc.player.networkHandler.sendChatCommand(cmd.startsWith("/") ? cmd.substring(1) : cmd);
            }
        }
        priceWatch(mc);
    }

    void priceWatch(MinecraftClient mc) {
        Config c = Config.I;
        if (mc.player.age % 10 != 0) return;
        if (mc.currentScreen != lastScreen) { alerted.clear(); lastScreen = mc.currentScreen; }
        if (!c.priceWatch || c.watchMaxUnit <= 0 || c.watchKeyword.isBlank() || !active()) return;
        if (!(mc.currentScreen instanceof HandledScreen<?> hs)) return;
        String kw = c.watchKeyword.toLowerCase();
        int containerSlots = hs.getScreenHandler().slots.size() - 36;
        for (int i = 0; i < containerSlots; i++) {
            Slot slot = hs.getScreenHandler().slots.get(i);
            ItemStack st = slot.getStack();
            if (st.isEmpty() || !st.getName().getString().toLowerCase().contains(kw)) continue;
            LoreComponent lore = st.getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT);
            for (Text line : lore.lines()) {
                Matcher m = PRICE.matcher(Formatting.strip(line.getString()));
                if (!m.find()) continue;
                double unit = Util.parse(m.group(1)) / Math.max(1, st.getCount());
                String key = i + ":" + unit;
                if (unit <= c.watchMaxUnit && alerted.add(key)) {
                    mc.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
                    mc.player.sendMessage(Text.literal("§a[PriceWatch] §f" + st.getName().getString() + " x" + st.getCount()
                            + " at " + Util.fmt(unit) + " each (slot " + i + ")"), false);
                }
                break;
            }
        }
    }
}
