package dev.skyzoom;

import dev.skyzoom.module.Modules;
import dev.skyzoom.module.Setting;
import net.minecraft.client.multiplayer.ClientLevel;

public final class SkyState {
    private static final String[] NAMES = {"Sunrise", "Noon", "Sunset", "Midnight"};
    private static final long[] PRESETS = {23000, 6000, 12000, 18000};
    private static int idx = -1;

    public static String cycleTime() {
        idx++;
        if (idx >= PRESETS.length) {
            idx = -1;
            Modules.TIME.enabled = false;
            return "Time changer: off";
        }
        Modules.TIME.enabled = true;
        Modules.TIME_TICKS.setExact(PRESETS[idx]);
        return "Time: " + NAMES[idx];
    }

    public static String cycleWeather() {
        Setting.Choice c = Modules.WEATHER_MODE;
        if (!Modules.WEATHER.enabled) {
            Modules.WEATHER.enabled = true;
            c.index = 0;
        } else if (c.index == c.options.length - 1) {
            Modules.WEATHER.enabled = false;
            return "Weather changer: off";
        } else {
            c.cycle(1);
        }
        return "Weather: " + c.get();
    }

    public static void apply(ClientLevel level) {
        if (Modules.TIME.enabled) {
            level.setTimeFromServer(level.getGameTime(), (long) Modules.TIME_TICKS.value, false);
        }
        if (Modules.WEATHER.enabled) {
            int i = Modules.WEATHER_MODE.index;
            level.setRainLevel(i >= 1 ? 1f : 0f);
            level.setThunderLevel(i >= 2 ? 1f : 0f);
        }
    }
}
