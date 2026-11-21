package dev.donutplus;

/** Supplies override colours to the sky mixins. Returns null = leave vanilla. */
public class SkyManager {
    private static int base() {
        Config c = Config.I;
        if (c.skyMode == 2) {
            double period = 20000.0 / Math.max(0.05, c.rainbowSpeed);
            return Util.hsv((float) ((System.currentTimeMillis() % (long) period) / period), 0.55f, 1f);
        }
        return Util.color(c.skyColor, 0xFF69B4);
    }

    public static Integer sky() {
        return Config.I.skyMode == 0 ? null : (0xFF000000 | base());
    }

    public static Integer fog() {
        Config c = Config.I;
        if (c.skyMode == 0 || !c.skyFog) return null;
        return 0xFF000000 | (c.skyMode == 2 ? base() : Util.color(c.fogColor, 0xFF69B4));
    }

    public static Integer clouds() {
        Config c = Config.I;
        if (!c.skyClouds) return null;
        return 0xFF000000 | Util.color(c.cloudColor, 0xFFFFFF);
    }
}
