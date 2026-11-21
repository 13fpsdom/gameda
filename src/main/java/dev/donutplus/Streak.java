package dev.donutplus;

public class Streak {
    public static void win() {
        Config c = Config.I;
        c.wins++; c.streak++; c.bestStreak = Math.max(c.bestStreak, c.streak);
        Config.save();
    }
    public static void loss() { Config c = Config.I; c.losses++; c.streak = 0; Config.save(); }
    public static void reset() { Config c = Config.I; c.streak = 0; c.wins = 0; c.losses = 0; Config.save(); }
}
