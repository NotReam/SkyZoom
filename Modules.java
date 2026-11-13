package dev.skyzoom.module;

import dev.skyzoom.Freecam;
import net.minecraft.client.Minecraft;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class Modules {
    // --- Zoom ---
    public static final Module ZOOM = new Module("Zoom", "Hold the zoom key (C). Scroll to adjust.", true);
    public static final Setting.Num ZOOM_LEVEL = ZOOM.add(new Setting.Num("Level", 1.5, 30, 4, 0.1));
    public static final Setting.Num ZOOM_SMOOTH = ZOOM.add(new Setting.Num("Smoothing", 0, 0.9, 0.35, 0.05));
    public static final Setting.Bool ZOOM_SCROLL = ZOOM.add(new Setting.Bool("Scroll adjust", true));

    // --- Freecam ---
    public static final Module FREECAM = new Module("Freecam", "Detach the camera from your player (F8).", false) {
        @Override public boolean isEnabled() { return Freecam.active; }
        @Override public void toggle() { Freecam.toggle(Minecraft.getInstance()); }
    };
    public static final Setting.Num FC_SPEED = FREECAM.add(new Setting.Num("Speed", 0.1, 3, 0.6, 0.1));
    public static final Setting.Num FC_SPRINT = FREECAM.add(new Setting.Num("Sprint x", 1, 6, 3, 0.5));
    public static final Setting.Num FC_SMOOTH = FREECAM.add(new Setting.Num("Smoothing", 0, 0.9, 0.7, 0.05));
    public static final Setting.Bool FC_SCROLL = FREECAM.add(new Setting.Bool("Scroll speed", true));

    // --- Sky ---
    public static final Module TIME = new Module("Time Changer", "Client-side time of day (K cycles presets).", false);
    public static final Setting.Num TIME_TICKS = TIME.add(new Setting.Num("Ticks", 0, 24000, 6000, 250));
    public static final Module WEATHER = new Module("Weather Changer", "Client-side weather (J cycles).", false);
    public static final Setting.Choice WEATHER_MODE = WEATHER.add(new Setting.Choice("Weather", 0, "Clear", "Rain", "Storm"));

    // --- HUD ---
    public static final Module FPS = new Module("FPS Counter", "Current, min, avg and max FPS.", true);
    public static final Setting.Bool FPS_STATS = FPS.add(new Setting.Bool("Min/Avg/Max", true));
    public static final Setting.Num FPS_WINDOW = FPS.add(new Setting.Num("Window (s)", 2, 30, 10, 1));
    public static final Module COORDS = new Module("Coordinates", "Shows your XYZ.", true);
    public static final Setting.Num COORD_DECIMALS = COORDS.add(new Setting.Num("Decimals", 0, 3, 1, 1));

    public static final Map<String, List<Module>> PANELS = new LinkedHashMap<>();
    static {
        PANELS.put("Render", List.of(ZOOM, FREECAM, TIME, WEATHER));
        PANELS.put("HUD", List.of(FPS, COORDS));
    }

    /** Turn everything off. */
    public static void panic() {
        for (List<Module> list : PANELS.values())
            for (Module m : list)
                if (m.isEnabled()) m.toggle();
    }

    private Modules() {}
}
