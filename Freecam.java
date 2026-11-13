package dev.skyzoom;

import dev.skyzoom.module.Modules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.util.Mth;

public final class Freecam {
    public static boolean active = false;
    /** Camera stays where it is, but you control your real player again. */
    public static boolean locked = false;
    public static double x, y, z, px, py, pz;
    public static float yaw, pitch;
    private static double vx, vy, vz;

    public static void toggle(Minecraft mc) {
        if (mc.player == null) return;
        active = !active;
        locked = false;
        if (active) {
            x = px = mc.player.getX();
            y = py = mc.player.getEyeY();
            z = pz = mc.player.getZ();
            yaw = mc.player.getYRot();
            pitch = mc.player.getXRot();
            vx = vy = vz = 0;
        }
    }

    public static void toggleLock() {
        if (active) locked = !locked;
    }

    public static boolean controlsCamera() { return active && !locked; }

    public static void tick(Minecraft mc) {
        if (!active) return;
        if (mc.player == null) { active = false; return; }
        px = x; py = y; pz = z;
        if (locked) { vx = vy = vz = 0; return; }

        double f = 0, s = 0, v = 0;
        if (mc.screen == null) {
            Options o = mc.options;
            f = (o.keyUp.isDown() ? 1 : 0) - (o.keyDown.isDown() ? 1 : 0);
            s = (o.keyLeft.isDown() ? 1 : 0) - (o.keyRight.isDown() ? 1 : 0);
            v = (o.keyJump.isDown() ? 1 : 0) - (o.keyShift.isDown() ? 1 : 0);
            if (o.keySprint.isDown()) {
                double m = Modules.FC_SPRINT.value;
                f *= m; s *= m; v *= m;
            }
        }
        double speed = Modules.FC_SPEED.value;
        double r = Math.toRadians(yaw);
        double tx = (-Math.sin(r) * f + Math.cos(r) * s) * speed;
        double tz = (Math.cos(r) * f + Math.sin(r) * s) * speed;
        double ty = v * speed;
        double a = 1.0 - Modules.FC_SMOOTH.value;
        vx += (tx - vx) * a; vy += (ty - vy) * a; vz += (tz - vz) * a;
        x += vx; y += vy; z += vz;
    }

    public static void turn(double yRot, double xRot) {
        yaw += (float) (yRot * 0.15);
        pitch = Mth.clamp(pitch + (float) (xRot * 0.15), -90f, 90f);
    }

    public static void scroll(double amount) {
        if (Modules.FC_SCROLL.value) Modules.FC_SPEED.setExact(Modules.FC_SPEED.value * Math.pow(1.15, amount));
    }

    public static double x(float pt) { return Mth.lerp(pt, px, x); }
    public static double y(float pt) { return Mth.lerp(pt, py, y); }
    public static double z(float pt) { return Mth.lerp(pt, pz, z); }
}
