package dev.donutplus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Config {
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("donutplus.json");
    public static Config I = new Config();

    public boolean forceEnable = false;

    // HUD
    public boolean hud = true, hudBalance = true, hudSession = true, hudDaily = true, hudStreak = true,
            hudTotems = true, hudCoords = false, hudAuction = true;
    public int hudX = 4, hudY = 4, hudBgAlpha = 144;
    public double dailyGoal = 0;
    public boolean armorWarn = true;
    public int armorWarnPercent = 10;

    // payment chat
    public boolean payColors = true, compactPay = false;
    public String colSmall = "#AAAAAA", colMedium = "#55FF55", colBig = "#FFAA00", colHuge = "#FF55FF", colOut = "#FF5555";
    public double tierMedium = 10_000, tierBig = 100_000, tierHuge = 1_000_000;

    // popup
    public boolean popup = true, popupSound = true;
    public double popupMin = 1_000_000;
    public int popupSeconds = 5;

    // filters
    public boolean filterSmall = false, filterSpam = false;
    public double smallBelow = 1000;
    public int spamMax = 3, spamWindowSec = 10;

    // pay guard + confirmation screen
    public boolean payGuard = true;
    public double payConfirmAbove = 1_000_000;
    public int confirmDelayMs = 1000;
    public List<String> guardNames = new ArrayList<>();

    // streak
    public int streak = 0, bestStreak = 0, wins = 0, losses = 0;
    public String winRegex = "(?i)\\byou (?:have )?won\\b";
    public String lossRegex = "(?i)\\byou (?:have )?lost\\b";

    // trading helpers
    public boolean unitPrice = true, priceWatch = false;
    public String watchKeyword = "";
    public double watchMaxUnit = 0;
    public boolean adBlock = true;
    public List<String> adPatterns = new ArrayList<>(List.of("(?i)discord\\.gg/", "(?i)store\\.donutsmp"));
    public String[] hotkeyCommands = {"ah", "bal", "shop", "sell"};

    // chat parsing - verify against real DonutSMP messages
    public String recvRegex = "(\\w{3,16}) (?:has )?paid you \\$([\\d.,]+[KMBkmb]?)";
    public String sentRegex = "You (?:have )?paid (\\w{3,16}) \\$([\\d.,]+[KMBkmb]?)";
    public String sellRegex = "(?i)you sold .*? for \\$([\\d.,]+[KMBkmb]?)";
    public String balanceRegex = "(?i)(?:balance|money)[: ]+\\$([\\d.,]+[KMBkmb]?)";

    // sky (works on every server)
    public int skyMode = 0;                  // 0 off, 1 solid, 2 rainbow
    public String skyColor = "#FF69B4";
    public boolean skyFog = true;
    public String fogColor = "#FF69B4";
    public boolean skyClouds = false;
    public String cloudColor = "#FFFFFF";
    public double rainbowSpeed = 1.0;

    // texture editor
    public String lastTextureId = "item/totem_of_undying";

    public static void load() {
        try {
            if (Files.exists(PATH)) {
                Config c = GSON.fromJson(Files.readString(PATH), Config.class);
                if (c != null) I = c;
            }
        } catch (Exception e) { e.printStackTrace(); }
        save();
    }

    public static void save() {
        try { Files.writeString(PATH, GSON.toJson(I)); } catch (Exception e) { e.printStackTrace(); }
    }
}
