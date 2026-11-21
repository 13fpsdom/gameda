package dev.donutplus;

import java.lang.reflect.Method;

public class FilePicker {
    /** Opens a native file dialog (LWJGL tinyfd, shipped with Minecraft). Returns null if cancelled/unavailable. */
    public static String pick(String title) {
        try {
            Class<?> c = Class.forName("org.lwjgl.util.tinyfd.TinyFileDialogs");
            Method m = c.getMethod("tinyfd_openFileDialog", CharSequence.class, CharSequence.class,
                    org.lwjgl.PointerBuffer.class, CharSequence.class, boolean.class);
            return (String) m.invoke(null, title, "", null, null, false);
        } catch (Throwable t) {
            return null;
        }
    }
}
