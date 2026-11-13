package dev.skyzoom.mixin;

import dev.skyzoom.Freecam;
import dev.skyzoom.SkyZoom;
import dev.skyzoom.ZoomState;
import dev.skyzoom.module.Modules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void skyzoom$scroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
        if (yOffset == 0 || Minecraft.getInstance().screen != null) return;
        if (Freecam.controlsCamera() && Modules.FC_SCROLL.value) {
            Freecam.scroll(yOffset);
            ci.cancel();
        } else if (Modules.ZOOM.enabled && Modules.ZOOM_SCROLL.value
                && SkyZoom.ZOOM != null && SkyZoom.ZOOM.isDown()) {
            ZoomState.scroll(yOffset);
            ci.cancel();
        }
    }
}
