package dev.skyzoom.gui;

import dev.skyzoom.module.Module;
import dev.skyzoom.module.Modules;
import dev.skyzoom.module.Setting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Cyberpunk-style panels. LMB = toggle, RMB = settings. */
public class ClickGui extends Screen {
    private static final int PW = 140, RH = 16, HEAD = 20, GAP = 10, TOP = 26;

    private record Hit(int x, int y, int w, int h, Module m, Setting s) {
        boolean in(double mx, double my) { return mx >= x && mx < x + w && my >= y && my < y + h; }
    }

    private final List<Hit> hits = new ArrayList<>();
    private final Set<Module> open = new HashSet<>();
    private final Map<Object, Float> anim = new HashMap<>();
    private Hit dragging;
    private long opened = System.currentTimeMillis();

    public ClickGui() { super(Component.literal("SkyZoom")); }

    @Override public boolean isPauseScreen() { return false; }

    @Override
    protected void init() { opened = System.currentTimeMillis(); }

    private float hover(Object key, boolean on) {
        float v = anim.getOrDefault(key, 0f);
        v += ((on ? 1f : 0f) - v) * 0.3f;
        anim.put(key, v);
        return v;
    }

    private void glitchText(GuiGraphicsExtractor g, String s, int x, int y, int color, long now, float e) {
        if ((now / 1000) % 4 == 0 && now % 1000 < 110) {
            g.text(this.font, s, x - 2, y, Theme.alpha(Theme.RED, e), false);
            g.text(this.font, s, x + 2, y, Theme.alpha(Theme.CYAN, e), false);
        }
        g.text(this.font, s, x, y, Theme.alpha(color, e), false);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mx, int my, float delta) {
        super.extractRenderState(g, mx, my, delta);
        long now = System.currentTimeMillis();
        float in = Math.min(1f, (now - opened) / 320f);
        float e = 1 - (1 - in) * (1 - in);
        int oy = (int) ((1 - e) * -16);

        // atmosphere: tint, scanlines, moving scan band
        g.fill(0, 0, this.width, this.height, Theme.alpha(0xFF02040A, 0.55f * e));
        for (int y = 0; y < this.height; y += 3) g.fill(0, y, this.width, y + 1, Theme.alpha(0xFF000000, 0.18f * e));
        int by = (int) ((now / 12) % (this.height + 40)) - 20;
        g.fill(0, by, this.width, by + 2, Theme.alpha(Theme.CYAN, 0.10f * e));

        // top bar
        g.fill(0, 0, this.width, 15, Theme.alpha(0xFF05060A, 0.92f * e));
        g.fill(0, 14, this.width, 15, Theme.alpha(Theme.YELLOW, 0.9f * e));
        glitchText(g, "SKYZOOM // CYBERDECK", 10, 4, Theme.YELLOW, now, e);
        String st = ((now / 500) % 2 == 0 ? "* " : "  ") + "SYSTEM ONLINE";
        g.text(this.font, st, this.width - this.font.width(st) - 10, 4, Theme.alpha(Theme.CYAN, e), false);

        hits.clear();
        String tip = null;
        int x = 12;
        float pulse = 0.5f + 0.5f * (float) Math.sin(now / 300.0);

        for (Map.Entry<String, List<Module>> panel : Modules.PANELS.entrySet()) {
            List<Module> mods = panel.getValue();
            int h = HEAD + 4;
            int on = 0;
            for (Module m : mods) {
                h += RH + (open.contains(m) ? m.settings.size() * RH : 0);
                if (m.isEnabled()) on++;
            }
            int y = TOP + oy;

            Theme.chamfer(g, x, y, PW, h, 6, Theme.alpha(Theme.BG, e));
            Theme.frame(g, x, y, PW, h, 6, Theme.alpha(Theme.YELLOW, 0.55f * e));
            g.fill(x + 6, y, x + PW, y + 2, Theme.alpha(Theme.YELLOW, e));
            g.text(this.font, "// " + panel.getKey().toUpperCase(), x + 9, y + 7, Theme.alpha(Theme.YELLOW, e), false);
            String cnt = on + "/" + mods.size();
            g.text(this.font, cnt, x + PW - this.font.width(cnt) - 8, y + 7, Theme.alpha(Theme.CYAN, e), false);
            g.fill(x + 5, y + HEAD - 1, x + PW - 5, y + HEAD, Theme.alpha(Theme.DIM, 0.5f * e));

            int cy = y + HEAD;
            for (Module m : mods) {
                Hit h0 = new Hit(x, cy, PW, RH, m, null);
                boolean hv = h0.in(mx, my);
                float a = hover(m, hv);
                if (hv) tip = m.description;
                boolean en = m.isEnabled();

                if (en) g.fill(x + 2, cy, x + PW - 2, cy + RH, Theme.alpha(Theme.YELLOW, (0.10f + 0.10f * pulse) * e));
                else if (a > 0.02f) g.fill(x + 2, cy, x + PW - 2, cy + RH, Theme.alpha(Theme.CYAN, 0.10f * a * e));
                int bar = en ? Theme.YELLOW : (hv ? Theme.CYAN : Theme.DIM);
                g.fill(x + 2, cy, x + 4, cy + RH, Theme.alpha(bar, e));

                int tc = en ? Theme.YELLOW : (hv ? Theme.WHITE : Theme.TEXT);
                g.text(this.font, m.name, x + 9 + (int) (3 * a), cy + 4, Theme.alpha(tc, e), false);

                String status = en ? "ON" : "OFF";
                int sw = this.font.width(status);
                g.text(this.font, status, x + PW - sw - 8, cy + 4, Theme.alpha(en ? Theme.YELLOW : Theme.DIM, e), false);
                if (!m.settings.isEmpty()) {
                    g.text(this.font, open.contains(m) ? "-" : "+", x + PW - sw - 20, cy + 4, Theme.alpha(Theme.CYAN, e), false);
                }
                hits.add(h0);
                cy += RH;

                if (open.contains(m)) {
                    for (Setting s : m.settings) {
                        Hit hs = new Hit(x, cy, PW, RH, m, s);
                        boolean sh = hs.in(mx, my);
                        float sa = hover(s, sh);
                        g.fill(x + 3, cy, x + PW - 3, cy + RH, Theme.alpha(0xFF000000, 0.35f * e));
                        if (sa > 0.02f) g.fill(x + 3, cy, x + PW - 3, cy + RH, Theme.alpha(Theme.CYAN, 0.07f * sa * e));
                        g.fill(x + 6, cy, x + 7, cy + RH, Theme.alpha(Theme.CYAN, 0.5f * e));
                        g.text(this.font, s.name, x + 12, cy + 3, Theme.alpha(sh ? Theme.WHITE : Theme.TEXT, e), false);

                        String v = s.display();
                        int vc = Theme.CYAN;
                        if (s instanceof Setting.Bool bo) vc = bo.value ? Theme.YELLOW : Theme.RED;
                        else if (s instanceof Setting.Choice) vc = Theme.YELLOW;
                        g.text(this.font, v, x + PW - this.font.width(v) - 8, cy + 3, Theme.alpha(vc, e), false);

                        if (s instanceof Setting.Num n) {
                            int tx0 = x + 12, tx1 = x + PW - 8;
                            double frac = (n.value - n.min) / (n.max - n.min);
                            int px = tx0 + (int) ((tx1 - tx0) * frac);
                            g.fill(tx0, cy + RH - 4, tx1, cy + RH - 3, Theme.alpha(Theme.DIM, 0.7f * e));
                            g.fill(tx0, cy + RH - 4, px, cy + RH - 3, Theme.alpha(Theme.CYAN, e));
                            g.fill(px - 1, cy + RH - 6, px + 1, cy + RH - 1, Theme.alpha(Theme.YELLOW, e));
                        }
                        hits.add(hs);
                        cy += RH;
                    }
                }
            }
            x += PW + GAP;
        }

        // bottom status bar
        int by0 = this.height - 18;
        g.fill(0, by0, this.width, this.height, Theme.alpha(0xFF05060A, 0.92f * e));
        g.fill(0, by0, this.width, by0 + 1, Theme.alpha(Theme.YELLOW, 0.9f * e));
        String left = tip != null ? ">> " + tip : ">> LMB toggle  //  RMB config  //  R-SHIFT close";
        g.text(this.font, left, 10, by0 + 5, Theme.alpha(Theme.CYAN, e), false);
        String right = "SKYZOOM v1.1 // MC 26.3";
        g.text(this.font, right, this.width - this.font.width(right) - 10, by0 + 5, Theme.alpha(Theme.DIM, e), false);
    }

    private void setSlider(Hit h, double mx) {
        Setting.Num n = (Setting.Num) h.s();
        double frac = Math.max(0, Math.min(1, (mx - (h.x() + 12)) / (PW - 20)));
        n.set(n.min + frac * (n.max - n.min));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent e, boolean doubleClick) {
        for (Hit h : hits) {
            if (!h.in(e.x(), e.y())) continue;
            int b = e.button();
            if (h.s() == null) {
                if (b == 0) h.m().toggle();
                else if (b == 1 && !h.m().settings.isEmpty()) {
                    if (!open.remove(h.m())) open.add(h.m());
                }
            } else if (h.s() instanceof Setting.Num) {
                dragging = h;
                setSlider(h, e.x());
            } else if (h.s() instanceof Setting.Bool bo) {
                bo.value = !bo.value;
            } else if (h.s() instanceof Setting.Choice c) {
                c.cycle(b == 1 ? -1 : 1);
            }
            return true;
        }
        return super.mouseClicked(e, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent e, double dx, double dy) {
        if (dragging != null) { setSlider(dragging, e.x()); return true; }
        return super.mouseDragged(e, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent e) {
        dragging = null;
        return super.mouseReleased(e);
    }

    @Override
    public boolean keyPressed(KeyEvent e) {
        if (e.key() == GLFW.GLFW_KEY_RIGHT_SHIFT) { this.onClose(); return true; }
        return super.keyPressed(e);
    }
}
