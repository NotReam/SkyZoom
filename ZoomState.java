package dev.skyzoom;

import dev.skyzoom.module.Modules;
import net.minecraft.util.Mth;

public final class ZoomState {
    private static double prev = 1.0, cur = 1.0;

    public static void tick(boolean zooming) {
        prev = cur;
        double target = (zooming && Modules.ZOOM.enabled) ? 1.0 / Modules.ZOOM_LEVEL.value : 1.0;
        cur += (target - cur) * (1.0 - Modules.ZOOM_SMOOTH.value);
    }

    public static double multiplier(float partialTick) {
        return Mth.lerp(partialTick, prev, cur);
    }

    public static void scroll(double amount) {
        Modules.ZOOM_LEVEL.setExact(Modules.ZOOM_LEVEL.value * Math.pow(1.15, amount));
    }
}
