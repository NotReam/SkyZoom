package dev.skyzoom.mixin;

import dev.skyzoom.Freecam;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    private void skyzoom$turn(double yRot, double xRot, CallbackInfo ci) {
        if (Freecam.controlsCamera() && (Object) this == Minecraft.getInstance().player) {
            Freecam.turn(yRot, xRot);
            ci.cancel();
        }
    }
}
