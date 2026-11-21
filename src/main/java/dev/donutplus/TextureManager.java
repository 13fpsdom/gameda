package dev.donutplus;

import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;

/** Manages a generated folder resource pack ("DonutPlus Textures") that holds your texture overrides. */
public class TextureManager {
    public static final String PACK = "DonutPlus Textures";
    public static final String PACK_ID = "file/" + PACK;

    private static MinecraftClient mc() { return MinecraftClient.getInstance(); }
    public static Path packsDir() { return mc().runDirectory.toPath().resolve("resourcepacks"); }
    public static Path dir() { return packsDir().resolve(PACK); }

    private static void ensureMeta() throws IOException {
        Path meta = dir().resolve("pack.mcmeta");
        if (!Files.exists(meta)) {
            Files.createDirectories(dir());
            Files.writeString(meta, "{\"pack\":{\"min_format\":[69,0],\"max_format\":99,\"description\":\"DonutPlus texture overrides\"}}");
        }
    }

    /** texId like item/totem_of_undying (no .png). */
    public static String apply(String texId, Path png) {
        try {
            texId = texId.trim().toLowerCase();
            if (texId.endsWith(".png")) texId = texId.substring(0, texId.length() - 4);
            if (!texId.matches("[a-z0-9_./-]+") || texId.contains("..")) return "Invalid texture id.";
            if (!Files.isRegularFile(png)) return "PNG file not found.";
            if (javax.imageio.ImageIO.read(png.toFile()) == null) return "That file is not a valid PNG image.";
            ensureMeta();
            Path out = dir().resolve("assets/minecraft/textures/" + texId + ".png");
            Files.createDirectories(out.getParent());
            Files.copy(png, out, StandardCopyOption.REPLACE_EXISTING);
            reload();
            return "Applied " + texId + " (use the same size as the original).";
        } catch (Exception e) {
            return "Failed: " + e.getMessage();
        }
    }

    public static String reset(String texId) {
        try {
            Path f = dir().resolve("assets/minecraft/textures/" + texId.trim().toLowerCase() + ".png");
            if (!Files.deleteIfExists(f)) return "No override for that texture.";
            reload();
            return "Reset " + texId;
        } catch (Exception e) { return "Failed: " + e.getMessage(); }
    }

    public static String resetAll() {
        try {
            Path a = dir().resolve("assets");
            if (Files.exists(a)) try (Stream<Path> w = Files.walk(a)) { w.sorted(Comparator.reverseOrder()).forEach(p -> p.toFile().delete()); }
            reload();
            return "All overrides removed.";
        } catch (Exception e) { return "Failed: " + e.getMessage(); }
    }

    /** Scan, enable the pack if needed, and reload resources. */
    public static void reload() {
        MinecraftClient c = mc();
        ResourcePackManager rpm = c.getResourcePackManager();
        rpm.scanPacks();
        boolean was = rpm.getEnabledIds().contains(PACK_ID);
        if (!was) rpm.enable(PACK_ID);
        c.options.refreshResourcePacks(rpm);   // reloads if the enabled list changed
        if (was) c.reloadResources();
    }

    /** Copies a pack zip into resourcepacks and enables it. */
    public static String importPack(Path zip) {
        try {
            if (!Files.isRegularFile(zip) || !zip.getFileName().toString().toLowerCase().endsWith(".zip")) return "Pick a .zip resource pack.";
            Files.createDirectories(packsDir());
            Path dest = packsDir().resolve(zip.getFileName().toString());
            Files.copy(zip, dest, StandardCopyOption.REPLACE_EXISTING);
            ResourcePackManager rpm = mc().getResourcePackManager();
            rpm.scanPacks();
            String id = "file/" + dest.getFileName();
            if (!rpm.enable(id)) return "Copied, but could not enable it. Check the pack format.";
            mc().options.refreshResourcePacks(rpm);
            return "Imported and enabled " + dest.getFileName();
        } catch (Exception e) { return "Failed: " + e.getMessage(); }
    }
}
