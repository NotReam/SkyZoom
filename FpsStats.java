package dev.skyzoom;

/** Rolling 10-second window of per-frame FPS. */
public final class FpsStats {
    private static final int CAP = 16384;
    
    private static final long[] times = new long[CAP];
    private static final float[] fps = new float[CAP];
    private static int head = 0, count = 0;
    private static long last = 0, lastCalc = 0;
    public static int min, avg, max, now;
    private static long window() { return (long) (dev.skyzoom.module.Modules.FPS_WINDOW.value * 1e9); }

    public static void frame() {
        long t = System.nanoTime();
        if (last != 0 && t > last) {
            float f = 1e9f / (t - last);
            times[head] = t;
            fps[head] = f;
            head = (head + 1) % CAP;
            if (count < CAP) count++;
            now = Math.round(f);
        }
        last = t;
        while (count > 0 && t - times[(head - count + CAP) % CAP] > window()) count--;
        if (t - lastCalc > 250_000_000L && count > 1) {
            lastCalc = t;
            float lo = Float.MAX_VALUE, hi = 0;
            for (int i = 0; i < count; i++) {
                float f = fps[(head - count + i + CAP) % CAP];
                if (f < lo) lo = f;
                if (f > hi) hi = f;
            }
            double span = (t - times[(head - count + CAP) % CAP]) / 1e9;
            min = Math.round(lo);
            max = Math.round(hi);
            avg = span > 0 ? (int) Math.round(count / span) : 0;
        }
    }
}
