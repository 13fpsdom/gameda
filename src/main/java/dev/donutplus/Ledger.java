package dev.donutplus;

import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Ledger {
    public static class Entry { public long time; public String type; public String player; public double amount; }

    static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("donutplus_ledger.json");
    public static List<Entry> entries = new ArrayList<>();
    public static double balance = -1;           // -1 = unknown
    public static final long SESSION_START = System.currentTimeMillis();

    public static void load() {
        try {
            if (Files.exists(PATH)) {
                List<Entry> l = Config.GSON.fromJson(Files.readString(PATH), new TypeToken<List<Entry>>() {}.getType());
                if (l != null) entries = l;
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void save() {
        try {
            while (entries.size() > 3000) entries.remove(0);
            Files.writeString(PATH, Config.GSON.toJson(entries));
        } catch (Exception e) { e.printStackTrace(); }
    }

    public static void add(String type, String player, double amount) {
        Entry e = new Entry();
        e.time = System.currentTimeMillis(); e.type = type; e.player = player; e.amount = amount;
        entries.add(e);
        if (balance >= 0) balance += type.equals("OUT") ? -amount : amount;
        save();
    }

    public static double sum(String type, long since) {
        double t = 0;
        for (Entry e : entries) if (e.type.equals(type) && e.time >= since) t += e.amount;
        return t;
    }

    public static double sumAll(long since, boolean income) {
        return income ? sum("IN", since) + sum("SELL", since) : sum("OUT", since);
    }

    public static long dayStart() {
        return java.time.LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static double profitToday() {
        long d = dayStart();
        return sumAll(d, true) - sumAll(d, false);
    }
}
