package dev.donutplus;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

public class Hud {
    public static void render(DrawContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Config c = Config.I;
        if (mc.player == null || mc.options.hudHidden || !DonutPlusClient.active()) return;
        TextRenderer tr = mc.textRenderer;

        Popup.render(ctx);

        if (c.hud) {
            List<String> lines = new ArrayList<>();
            if (c.hudBalance)
                lines.add("§6Balance: §f" + (Ledger.balance < 0 ? "unknown (/bal)" : Util.fmt(Ledger.balance)));
            if (c.hudDaily) {
                double p = Ledger.profitToday();
                String s = "§7Today: " + (p >= 0 ? "§a+" : "§c-") + Util.fmt(Math.abs(p));
                if (c.dailyGoal > 0) s += " §8/ " + Util.fmt(c.dailyGoal) + String.format(" §7(%.0f%%)", 100 * p / c.dailyGoal);
                lines.add(s);
            }
            if (c.hudSession) {
                double in = Ledger.sumAll(Ledger.SESSION_START, true), out = Ledger.sumAll(Ledger.SESSION_START, false);
                lines.add("§7Session: §a+" + Util.fmt(in) + " §c-" + Util.fmt(out));
            }
            if (c.hudStreak)
                lines.add("§bWin streak: §f" + c.streak + " §8(best " + c.bestStreak + ", " + c.wins + "W/" + c.losses + "L)");
            if (c.hudTotems) {
                PlayerInventory inv = mc.player.getInventory();
                int n = 0;
                for (int i = 0; i < inv.size(); i++) {
                    ItemStack s = inv.getStack(i);
                    if (s.isOf(Items.TOTEM_OF_UNDYING)) n += s.getCount();
                }
                lines.add("§eTotems: §f" + n);
            }
            if (c.hudCoords)
                lines.add(String.format("§bXYZ: §f%d %d %d", (int) mc.player.getX(), (int) mc.player.getY(), (int) mc.player.getZ()));
            if (c.hudAuction && Auction.active) {
                Auction.Bid t = Auction.top();
                lines.add("§dAuction: §f" + Auction.item + " §7" + Util.clock(Auction.endTime - System.currentTimeMillis())
                        + (t == null ? " §8no bids" : " §a" + Util.fmt(t.amount()) + " §7" + t.player()));
            }
            if (!lines.isEmpty()) {
                int w = 0;
                for (String l : lines) w = Math.max(w, tr.getWidth(l));
                int x = c.hudX, y = c.hudY;
                int alpha = Math.max(0, Math.min(255, c.hudBgAlpha));
                if (alpha > 0) ctx.fill(x - 3, y - 3, x + w + 3, y + lines.size() * 10 + 1, alpha << 24);
                for (String l : lines) { ctx.drawText(tr, l, x, y, 0xFFFFFFFF, true); y += 10; }
            }
        }

        if (c.armorWarn) {
            EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
            String[] names = {"Helmet", "Chestplate", "Leggings", "Boots"};
            int y = ctx.getScaledWindowHeight() / 2 + 30;
            for (int i = 0; i < 4; i++) {
                ItemStack s = mc.player.getEquippedStack(slots[i]);
                if (s.isEmpty() || !s.isDamageable()) continue;
                int pct = (int) (100.0 * (s.getMaxDamage() - s.getDamage()) / s.getMaxDamage());
                if (pct <= c.armorWarnPercent) {
                    ctx.drawCenteredTextWithShadow(tr, "§c" + names[i] + " durability " + pct + "%", ctx.getScaledWindowWidth() / 2, y, 0xFFFFFFFF);
                    y += 10;
                }
            }
        }
    }
}
