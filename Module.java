package dev.skyzoom.module;

import java.util.ArrayList;
import java.util.List;

public class Module {
    public final String name, description;
    public boolean enabled;
    public final List<Setting> settings = new ArrayList<>();

    public Module(String name, String description, boolean enabled) {
        this.name = name; this.description = description; this.enabled = enabled;
    }
    public boolean isEnabled() { return enabled; }
    public void toggle() { enabled = !enabled; }
    public <T extends Setting> T add(T s) { settings.add(s); return s; }
}
