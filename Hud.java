package dev.skyzoom;

import dev.skyzoom.gui.Theme;
import dev.skyzoom.module.Modules;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.ArrayList;
import java.util.List;

public final class Hud {
    public static boolean visible = true;
    private static String toast = "";
    private static long toastUntil = 0;

    private record Seg(String t, int c) {}

    public static void toast(String msg) {
        toast = msg;
        toastUntil = System.currentTimeMillis() + 2000;
    }

    private static int fpsColor(int f) {
        return f >= 60 ? Theme.CYAN : (f >= 30 ? Theme.YELLOW : Theme.RED);
    }

    public static void render(GuiGraphicsExtractor g, DeltaTracker delta) {
        FpsStats.frame();
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        long now = System.currentTimeMillis();
        if (now < toastUntil) {
            float a = Math.min(1f, (toastUntil - now) / 400f);
            String t = ">> " + toast.toUpperCase();
            int w = mc.font.width(t);
            int x = mc.getWindow().getGuiScaledWidth() / 2 - w / 2;
            Theme.chamfer(g, x - 8, 20, w + 16, 16, 4, Theme.alpha(Theme.BG, a));
            Theme.frame(g, x - 8, 20, w + 16, 16, 4, Theme.alpha(Theme.YELLOW, a));
            g.text(mc.font, t, x, 24, Theme.alpha(Theme.YELLOW, a), false);
        }
        if (!visible) return;

        List<List<Seg>> lines = new ArrayList<>();
        if (Modules.FPS.enabled) {
            List<Seg> l = new ArrayList<>();
            l.add(new Seg("FPS ", Theme.YELLOW));
            l.add(new Seg(String.valueOf(FpsStats.now), fpsColor(FpsStats.now)));
            if (Modules.FPS_STATS.value) {
                l.add(new Seg("  MIN ", Theme.DIM));
                l.add(new Seg(String.valueOf(FpsStats.min), Theme.RED));
                l.add(new Seg("  AVG ", Theme.DIM));
                l.add(new Seg(String.valueOf(FpsStats.avg), Theme.WHITE));
                l.add(new Seg("  MAX ", Theme.DIM));
                l.add(new Seg(String.valueOf(FpsStats.max), Theme.CYAN));
            }
            lines.add(l);
        }
        String fmt = "%." + (int) Modules.COORD_DECIMALS.value + "f";
        if (Freecam.active) {
            List<Seg> l = new ArrayList<>();
            l.add(new Seg("FREECAM ", Theme.CYAN));
            if (Freecam.locked) l.add(new Seg("[LOCKED] ", Theme.RED));
            l.add(new Seg(String.format(fmt + "  " + fmt + "  " + fmt, Freecam.x, Freecam.y, Freecam.z), Theme.WHITE));
            lines.add(l);
        }
        if (Modules.COORDS.enabled) {
            List<Seg> l = new ArrayList<>();
            l.add(new Seg("XYZ ", Theme.YELLOW));
            l.add(new Seg(String.format(fmt + "  " + fmt + "  " + fmt,
                    mc.player.getX(), mc.player.getY(), mc.player.getZ()), Theme.WHITE));
            lines.add(l);
        }

        int y = 5;
        for (List<Seg> line : lines) {
            int w = 0;
            for (Seg s : line) w += mc.font.width(s.t());
            Theme.chamfer(g, 3, y - 2, w + 12, 13, 3, 0xB00A0B12);
            g.fill(3, y - 2, 5, y + 11, Theme.YELLOW);
            int x = 9;
            for (Seg s : line) {
                g.text(mc.font, s.t(), x, y + 1, s.c(), false);
                x += mc.font.width(s.t());
            }
            y += 16;
        }
    }
}
