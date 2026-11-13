package dev.skyzoom;

import com.mojang.blaze3d.platform.InputConstants;
import dev.skyzoom.gui.ClickGui;
import dev.skyzoom.module.Modules;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class SkyZoom implements ClientModInitializer {
    public static final String ID = "skyzoom";
    public static KeyMapping ZOOM;

    private static KeyMapping key(String name, int code, KeyMapping.Category cat) {
        return KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.skyzoom." + name, InputConstants.Type.KEYSYM, code, cat));
    }

    @Override
    public void onInitializeClient() {
        KeyMapping.Category cat = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(ID, "main"));
        KeyMapping gui = key("gui", GLFW.GLFW_KEY_RIGHT_SHIFT, cat);
        ZOOM = key("zoom", GLFW.GLFW_KEY_C, cat);
        KeyMapping sky = key("sky", GLFW.GLFW_KEY_K, cat);
        KeyMapping weather = key("weather", GLFW.GLFW_KEY_J, cat);
        KeyMapping freecam = key("freecam", GLFW.GLFW_KEY_F8, cat);
        KeyMapping lock = key("lock", GLFW.GLFW_KEY_UNKNOWN, cat);
        KeyMapping hud = key("hud", GLFW.GLFW_KEY_F9, cat);
        KeyMapping panic = key("panic", GLFW.GLFW_KEY_UNKNOWN, cat);

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (gui.consumeClick()) if (mc.screen == null) mc.setScreen(new ClickGui());
            ZoomState.tick(ZOOM.isDown() && mc.screen == null);
            while (sky.consumeClick()) Hud.toast(SkyState.cycleTime());
            while (weather.consumeClick()) Hud.toast(SkyState.cycleWeather());
            while (hud.consumeClick()) Hud.visible = !Hud.visible;
            while (freecam.consumeClick()) {
                Freecam.toggle(mc);
                Hud.toast(Freecam.active ? "Freecam on" : "Freecam off");
            }
            while (lock.consumeClick()) {
                Freecam.toggleLock();
                if (Freecam.active) Hud.toast(Freecam.locked ? "Controlling player" : "Controlling camera");
            }
            while (panic.consumeClick()) {
                Modules.panic();
                Hud.toast("All modules off");
            }
            Freecam.tick(mc);
            if (mc.level != null) SkyState.apply(mc.level);
        });

        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(ID, "hud"), Hud::render);
    }
}
