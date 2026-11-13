package dev.skyzoom.module;

public abstract class Setting {
    public final String name;
    protected Setting(String name) { this.name = name; }
    public abstract String display();

    public static final class Num extends Setting {
        public final double min, max, step;
        public double value;
        public Num(String name, double min, double max, double value, double step) {
            super(name);
            this.min = min; this.max = max; this.value = value; this.step = step;
        }
        /** Snaps to the step (used by sliders). */
        public void set(double raw) { setExact(Math.round(raw / step) * step); }
        public void setExact(double v) { value = Math.max(min, Math.min(max, v)); }
        @Override public String display() {
            return step >= 1 ? String.valueOf(Math.round(value)) : String.format("%.2f", value);
        }
    }

    public static final class Bool extends Setting {
        public boolean value;
        public Bool(String name, boolean value) { super(name); this.value = value; }
        @Override public String display() { return value ? "ON" : "OFF"; }
    }

    public static final class Choice extends Setting {
        public final String[] options;
        public int index;
        public Choice(String name, int index, String... options) {
            super(name); this.options = options; this.index = index;
        }
        public void cycle(int dir) { index = Math.floorMod(index + dir, options.length); }
        public String get() { return options[index]; }
        @Override public String display() { return get(); }
    }
}
