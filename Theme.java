package dev.skyzoom.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/** Cyberpunk palette + chamfered-corner drawing helpers. */
public final class Theme {
    public static final int YELLOW = 0xFFFCEE0A;
    public static final int CYAN = 0xFF00F0FF;
    public static final int RED = 0xFFFF003C;
    public static final int BG = 0xF00A0B12;
    public static final int DIM = 0xFF6B6F7A;
    public static final int TEXT = 0xFFC9CCD6;
    public static final int WHITE = 0xFFFFFFFF;

    public static int alpha(int color, float a) {
        a = Math.max(0f, Math.min(1f, a));
        int al = (int) (((color >>> 24) & 0xFF) * a);
        return (al << 24) | (color & 0xFFFFFF);
    }

    /** Filled box with top-left and bottom-right corners cut off. */
    public static void chamfer(GuiGraphicsExtractor g, int x, int y, int w, int h, int cut, int color) {
        g.fill(x, y + cut, x + w, y + h - cut, color);
        for (int i = 0; i < cut; i++) {
            g.fill(x + cut - i, y + i, x + w, y + i + 1, color);
            g.fill(x, y + h - 1 - i, x + w - cut + i, y + h - i, color);
        }
    }

    /** 1px outline matching chamfer(). */
    public static void frame(GuiGraphicsExtractor g, int x, int y, int w, int h, int cut, int color) {
        g.fill(x + cut, y, x + w, y + 1, color);
        g.fill(x, y + cut, x + 1, y + h, color);
        g.fill(x, y + h - 1, x + w - cut, y + h, color);
        g.fill(x + w - 1, y, x + w, y + h - cut, color);
        for (int i = 0; i < cut; i++) {
            g.fill(x + cut - i - 1, y + i, x + cut - i, y + i + 1, color);
            g.fill(x + w - cut + i, y + h - 1 - i, x + w - cut + i + 1, y + h - i, color);
        }
    }

    private Theme() {}
}
