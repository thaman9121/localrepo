package com.nova.m13;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.*;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    NovaLauncherView nova;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(Color.rgb(5, 6, 10));
        getWindow().setNavigationBarColor(Color.rgb(5, 6, 10));
        nova = new NovaLauncherView(this);
        setContentView(nova);
    }

    @Override public void onBackPressed() {
        if (nova.page != 0) {
            nova.page = 0;
            nova.invalidate();
        } else {
            super.onBackPressed();
        }
    }

    static class AppEntry {
        String label, pkg;
        Drawable icon;
        AppEntry(String l, String p, Drawable i) { label = l; pkg = p; icon = i; }
    }

    static class NovaLauncherView extends View {
        final MainActivity a;
        final Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        final Paint stroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        final ArrayList<AppEntry> apps = new ArrayList<>();
        final HandlerClock clock = new HandlerClock();

        // 0 Home, 1 Apps, 2 Control Center, 3 Hub, 4 Widgets
        int page = 0;
        float downX, downY;
        String time = "--:--";

        final int bg = Color.rgb(5, 6, 10);
        final int panel = Color.rgb(14, 17, 24);
        final int panel2 = Color.rgb(20, 23, 32);
        final int text = Color.rgb(246, 247, 252);
        final int muted = Color.rgb(143, 150, 170);
        final int violet = Color.rgb(139, 92, 255);
        final int cyan = Color.rgb(82, 215, 255);
        final int green = Color.rgb(91, 225, 155);

        NovaLauncherView(MainActivity x) {
            super(x);
            a = x;
            setFocusable(true);
            stroke.setStyle(Paint.Style.STROKE);
            loadApps();
            clock.start();
        }

        void loadApps() {
            PackageManager pm = a.getPackageManager();
            Intent i = new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> list = pm.queryIntentActivities(i, 0);
            apps.clear();
            for (ResolveInfo r : list) {
                if (r.activityInfo.packageName.equals(a.getPackageName())) continue;
                apps.add(new AppEntry(r.loadLabel(pm).toString(), r.activityInfo.packageName, r.loadIcon(pm)));
            }
            Collections.sort(apps, (x, y) -> x.label.compareToIgnoreCase(y.label));
        }

        void refreshClock() {
            time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            invalidate();
        }

        @Override protected void onDraw(Canvas c) {
            super.onDraw(c);
            c.drawColor(bg);
            if (page == 0) drawHome(c);
            else if (page == 1) drawApps(c);
            else if (page == 2) drawControlCenter(c);
            else if (page == 3) drawHub(c);
            else drawWidgets(c);
        }

        void txt(Canvas c, String s, float x, float y, float size, int color, Paint.Align align) {
            p.setStyle(Paint.Style.FILL);
            p.setShader(null);
            p.setTextSize(size);
            p.setColor(color);
            p.setTextAlign(align);
            p.setTypeface(Typeface.create("sans", Typeface.NORMAL));
            c.drawText(s, x, y, p);
        }

        void bold(Canvas c, String s, float x, float y, float size, int color, Paint.Align align) {
            p.setStyle(Paint.Style.FILL);
            p.setShader(null);
            p.setTextSize(size);
            p.setColor(color);
            p.setTextAlign(align);
            p.setTypeface(Typeface.create("sans", Typeface.BOLD));
            c.drawText(s, x, y, p);
        }

        void round(Canvas c, float l, float t, float r, float b, float rad, int color) {
            p.setStyle(Paint.Style.FILL);
            p.setShader(null);
            p.setColor(color);
            c.drawRoundRect(l, t, r, b, rad, rad, p);
        }

        void border(Canvas c, float l, float t, float r, float b, float rad, int color) {
            stroke.setColor(color);
            stroke.setStrokeWidth(1.5f);
            c.drawRoundRect(l, t, r, b, rad, rad, stroke);
        }

        void drawGlow(Canvas c, float x, float y, float radius, int color) {
            p.setShader(new RadialGradient(x, y, radius,
                    new int[]{Color.argb(90, Color.red(color), Color.green(color), Color.blue(color)), Color.TRANSPARENT},
                    null, Shader.TileMode.CLAMP));
            c.drawCircle(x, y, radius, p);
            p.setShader(null);
        }

        void topBar(Canvas c, String title) {
            float w = getWidth();
            bold(c, title, 26, 48, 22, text, Paint.Align.LEFT);
            txt(c, time, w - 26, 48, 15, muted, Paint.Align.RIGHT);
        }

        void drawHome(Canvas c) {
            float w = getWidth(), h = getHeight();
            drawGlow(c, w * .82f, h * .20f, Math.min(w, h) * .46f, violet);
            drawGlow(c, w * .12f, h * .65f, Math.min(w, h) * .30f, cyan);

            txt(c, new SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(new Date()).toUpperCase(Locale.getDefault()),
                    w / 2, 55, 12, muted, Paint.Align.CENTER);
            bold(c, time, w / 2, 150, 70, text, Paint.Align.CENTER);
            txt(c, "NOVA", w / 2, 180, 13, cyan, Paint.Align.CENTER);

            // Weather card
            round(c, 22, 215, w - 22, 325, 24, panel);
            border(c, 22, 215, w - 22, 325, 24, Color.rgb(35, 41, 55));
            bold(c, "27°", 45, 262, 32, text, Paint.Align.LEFT);
            txt(c, "HYDERABAD", 47, 286, 11, muted, Paint.Align.LEFT);
            txt(c, "Clear  •  Feels like 29°", w - 45, 265, 13, text, Paint.Align.RIGHT);
            txt(c, "Good morning · 72% humidity", w - 45, 288, 11, muted, Paint.Align.RIGHT);

            // Smart cards
            float gap = 12;
            float cardW = (w - 44 - gap) / 2f;
            round(c, 22, 340, 22 + cardW, 450, 22, panel);
            round(c, 22 + cardW + gap, 340, w - 22, 450, 22, panel);
            bold(c, "FOCUS", 42, 370, 11, violet, Paint.Align.LEFT);
            txt(c, "Deep Work", 42, 402, 19, text, Paint.Align.LEFT);
            txt(c, "45 min session", 42, 426, 11, muted, Paint.Align.LEFT);
            bold(c, "MUSIC", 42 + cardW + gap, 370, 11, cyan, Paint.Align.LEFT);
            txt(c, "Nothing playing", 42 + cardW + gap, 402, 16, text, Paint.Align.LEFT);
            txt(c, "Tap to open player", 42 + cardW + gap, 426, 11, muted, Paint.Align.LEFT);

            // Bottom navigation
            drawBottomNav(c, 0);
            txt(c, "Swipe up · Control Center     Swipe left · Hub", w / 2, h - 9, 9, Color.rgb(92, 98, 115), Paint.Align.CENTER);
        }

        void drawBottomNav(Canvas c, int selected) {
            float w = getWidth(), h = getHeight();
            round(c, 18, h - 82, w - 18, h - 24, 27, Color.argb(235, 17, 20, 28));
            String[] labels = {"Home", "Apps", "NOVA", "Widgets"};
            float[] xs = {w * .13f, w * .38f, w * .62f, w * .87f};
            for (int i = 0; i < 4; i++) {
                if (i == selected) {
                    round(c, xs[i] - 23, h - 72, xs[i] + 23, h - 34, 19, Color.rgb(38, 32, 62));
                }
                if (i == 2) {
                    p.setStyle(Paint.Style.FILL); p.setColor(violet); c.drawCircle(xs[i], h - 53, 9, p);
                } else {
                    stroke.setColor(i == selected ? cyan : Color.rgb(105, 112, 130));
                    stroke.setStrokeWidth(2); c.drawCircle(xs[i], h - 53, 8, stroke);
                }
                txt(c, labels[i], xs[i], h - 27, 9, i == selected ? text : muted, Paint.Align.CENTER);
            }
        }

        void drawApps(Canvas c) {
            float w = getWidth(), h = getHeight();
            topBar(c, "Apps");
            round(c, 20, 70, w - 20, 122, 18, panel);
            txt(c, "⌕  Search apps", 40, 102, 14, muted, Paint.Align.LEFT);

            bold(c, "Favorites", 24, 155, 12, text, Paint.Align.LEFT);
            String[] fav = {"Phone", "Camera", "Gallery", "Music"};
            for (int i = 0; i < 4; i++) {
                float x = 45 + i * ((w - 90) / 3f);
                round(c, x - 22, 174, x + 22, 218, 16, panel2);
                txt(c, fav[i], x, 236, 9, muted, Paint.Align.CENTER);
                stroke.setColor(i == 0 ? cyan : Color.rgb(90, 98, 115));
                stroke.setStrokeWidth(2); c.drawCircle(x, 196, 10, stroke);
            }

            bold(c, "All Apps", 24, 275, 12, text, Paint.Align.LEFT);
            int cols = 4;
            float cellW = (w - 40) / cols;
            float startY = 310;
            int shown = Math.min(apps.size(), 28);
            for (int idx = 0; idx < shown; idx++) {
                int row = idx / cols, col = idx % cols;
                float x = 20 + cellW * col + cellW / 2;
                float y = startY + row * 78;
                Drawable d = apps.get(idx).icon;
                if (d != null) {
                    d.setBounds((int)x - 22, (int)y - 22, (int)x + 22, (int)y + 22);
                    d.draw(c);
                } else {
                    round(c, x - 22, y - 22, x + 22, y + 22, 14, panel2);
                }
                txt(c, trim(apps.get(idx).label, 11), x, y + 38, 9, text, Paint.Align.CENTER);
            }
            txt(c, "Swipe down to return", w / 2, h - 9, 9, muted, Paint.Align.CENTER);
        }

        String trim(String s, int n) { return s.length() > n ? s.substring(0, n - 1) + "…" : s; }

        void drawControlCenter(Canvas c) {
            float w = getWidth(), h = getHeight();
            drawGlow(c, w * .8f, 80, 240, cyan);
            topBar(c, "Control Center");
            txt(c, "Quick controls", 26, 78, 11, muted, Paint.Align.LEFT);

            float gap = 10;
            float tileW = (w - 52) / 2f;
            quickTile(c, 22, 92, 22 + tileW, 158, "Wi‑Fi", "Open settings", cyan, true, "wifi");
            quickTile(c, 30 + tileW, 92, w - 22, 158, "Bluetooth", "Open settings", violet, true, "bluetooth");
            quickTile(c, 22, 168, 22 + tileW, 234, "Mobile data", "Network", green, false, "data");
            quickTile(c, 30 + tileW, 168, w - 22, 234, "Location", "Device settings", cyan, false, "location");
            quickTile(c, 22, 244, 22 + tileW, 310, "Rotation", "Display", violet, false, "rotation");
            quickTile(c, 30 + tileW, 244, w - 22, 310, "Flashlight", "Device control", Color.rgb(255, 200, 80), false, "flash");

            round(c, 22, 332, w - 22, 406, 22, panel);
            txt(c, "Brightness", 43, 358, 12, text, Paint.Align.LEFT);
            txt(c, "78%", w - 43, 358, 12, cyan, Paint.Align.RIGHT);
            round(c, 43, 374, w - 43, 382, 4, Color.rgb(52, 57, 70));
            round(c, 43, 374, w * .78f, 382, 4, cyan);

            round(c, 22, 424, w - 22, 485, 22, panel);
            bold(c, "Device status", 43, 450, 12, text, Paint.Align.LEFT);
            txt(c, "Battery 78%     •     5G     •     Secure", 43, 471, 10, muted, Paint.Align.LEFT);
            txt(c, "Tap a tile to open Android settings", w / 2, h - 9, 9, muted, Paint.Align.CENTER);
        }

        void quickTile(Canvas c, float l, float t, float r, float b, String title, String sub,
                       int accent, boolean active, String action) {
            round(c, l, t, r, b, 20, active ? Color.rgb(29, 31, 45) : panel);
            border(c, l, t, r, b, 20, active ? Color.argb(120, Color.red(accent), Color.green(accent), Color.blue(accent)) : Color.rgb(34, 39, 51));
            p.setStyle(Paint.Style.FILL); p.setColor(accent); c.drawCircle(l + 28, t + 28, 9, p);
            bold(c, title, l + 48, t + 29, 12, text, Paint.Align.LEFT);
            txt(c, sub, l + 48, t + 48, 9, muted, Paint.Align.LEFT);
        }

        void drawHub(Canvas c) {
            float w = getWidth(), h = getHeight();
            drawGlow(c, w / 2, 205, 170, violet);
            topBar(c, "NOVA Hub");
            p.setStyle(Paint.Style.FILL); p.setColor(violet); c.drawCircle(w / 2, 195, 58, p);
            stroke.setColor(Color.argb(180, 82, 215, 255)); stroke.setStrokeWidth(2); c.drawCircle(w / 2, 195, 72, stroke);
            bold(c, "N", w / 2, 208, 34, text, Paint.Align.CENTER);
            txt(c, "Your command center", w / 2, 295, 15, text, Paint.Align.CENTER);
            txt(c, "Choose a mode", w / 2, 317, 10, muted, Paint.Align.CENTER);

            hubButton(c, 22, 340, w / 2 - 6, 410, "Focus Mode", "Deep work", violet);
            hubButton(c, w / 2 + 6, 340, w - 22, 410, "Student Mode", "Study tools", cyan);
            hubButton(c, 22, 420, w / 2 - 6, 490, "Gaming Mode", "Performance", green);
            hubButton(c, w / 2 + 6, 420, w - 22, 490, "Night Mode", "Low distraction", Color.rgb(110, 125, 255));
            txt(c, "NOVA AI · Settings · Appearance", w / 2, h - 14, 10, muted, Paint.Align.CENTER);
        }

        void hubButton(Canvas c, float l, float t, float r, float b, String title, String sub, int accent) {
            round(c, l, t, r, b, 20, panel);
            border(c, l, t, r, b, 20, Color.rgb(35, 40, 52));
            p.setColor(accent); p.setStyle(Paint.Style.FILL); c.drawCircle(l + 25, t + 27, 7, p);
            bold(c, title, l + 42, t + 29, 11, text, Paint.Align.LEFT);
            txt(c, sub, l + 42, t + 48, 9, muted, Paint.Align.LEFT);
        }

        void drawWidgets(Canvas c) {
            float w = getWidth(), h = getHeight();
            topBar(c, "Glance");
            txt(c, "Swipe right to return", 26, 76, 10, muted, Paint.Align.LEFT);

            round(c, 22, 96, w - 22, 190, 24, panel);
            bold(c, "TODAY", 43, 124, 11, cyan, Paint.Align.LEFT);
            txt(c, new SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault()).format(new Date()), 43, 150, 19, text, Paint.Align.LEFT);
            txt(c, "No urgent tasks", 43, 172, 10, muted, Paint.Align.LEFT);

            round(c, 22, 206, w - 22, 320, 24, panel);
            bold(c, "FOCUS", 43, 235, 11, violet, Paint.Align.LEFT);
            txt(c, "Deep Work", 43, 266, 23, text, Paint.Align.LEFT);
            txt(c, "45 min · notifications muted", 43, 291, 10, muted, Paint.Align.LEFT);
            round(c, w - 105, 252, w - 43, 282, 15, Color.rgb(39, 31, 61));
            txt(c, "START", w - 74, 272, 9, text, Paint.Align.CENTER);

            round(c, 22, 336, w - 22, 440, 24, panel);
            bold(c, "MUSIC", 43, 365, 11, cyan, Paint.Align.LEFT);
            txt(c, "Nothing playing", 43, 395, 17, text, Paint.Align.LEFT);
            txt(c, "Open your music app", 43, 417, 10, muted, Paint.Align.LEFT);

            round(c, 22, 456, w - 22, 545, 24, panel);
            bold(c, "NOVA TIP", 43, 484, 11, green, Paint.Align.LEFT);
            txt(c, "Swipe up anywhere to open", 43, 509, 14, text, Paint.Align.LEFT);
            txt(c, "Control Center", 43, 528, 10, muted, Paint.Align.LEFT);
            txt(c, "", w / 2, h - 5, 1, muted, Paint.Align.CENTER);
        }

        void openAction(String action) {
            try {
                Intent i;
                if (action.equals("wifi")) i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                else if (action.equals("bluetooth")) i = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                else if (action.equals("location")) i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                else if (action.equals("rotation")) i = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
                else if (action.equals("data")) i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                else if (action.equals("flash")) i = new Intent(Settings.ACTION_SETTINGS);
                else i = new Intent(Settings.ACTION_SETTINGS);
                a.startActivity(i);
            } catch (Exception ex) {
                Toast.makeText(a, "Android settings unavailable", Toast.LENGTH_SHORT).show();
            }
        }

        void launch(AppEntry e) {
            try {
                Intent i = a.getPackageManager().getLaunchIntentForPackage(e.pkg);
                if (i != null) a.startActivity(i);
            } catch (Exception ex) {
                Toast.makeText(a, "Could not open " + e.label, Toast.LENGTH_SHORT).show();
            }
        }

        @Override public boolean onTouchEvent(android.view.MotionEvent e) {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                downX = e.getX(); downY = e.getY(); return true;
            }
            if (e.getAction() == MotionEvent.ACTION_UP) {
                float dx = e.getX() - downX, dy = e.getY() - downY;
                float ax = Math.abs(dx), ay = Math.abs(dy);

                // Gesture navigation
                if (page == 0 && dy < -85 && ay > ax) { page = 2; invalidate(); return true; }
                if (page == 0 && dx < -85 && ax > ay) { page = 3; invalidate(); return true; }
                if (page == 0 && dx > 85 && ax > ay) { page = 4; invalidate(); return true; }
                if (page != 0 && dy > 85 && ay > ax) { page = 0; invalidate(); return true; }
                if (page == 4 && dx < -85 && ax > ay) { page = 0; invalidate(); return true; }

                if (page == 0 && downY > getHeight() - 110) {
                    float w = getWidth();
                    if (Math.abs(downX - w * .38f) < 55) { page = 1; invalidate(); return true; }
                    if (Math.abs(downX - w * .62f) < 55) { page = 3; invalidate(); return true; }
                    if (Math.abs(downX - w * .87f) < 55) { page = 4; invalidate(); return true; }
                }

                if (page == 1 && downY < 130 && downY > 65) {
                    Toast.makeText(a, "App search is ready for the next build", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (page == 1 && downY > 300 && ay < 40 && ax < 40) {
                    int cols = 4;
                    float cellW = (getWidth() - 40) / (float)cols;
                    int col = (int)((downX - 20) / cellW);
                    int row = (int)((downY - 288) / 78);
                    int idx = row * cols + col;
                    if (idx >= 0 && idx < apps.size() && idx < 28) { launch(apps.get(idx)); return true; }
                }

                if (page == 2 && downY >= 92 && downY <= 310 && ay < 35 && ax < 35) {
                    float tileW = (getWidth() - 52) / 2f;
                    int col = downX < getWidth() / 2 ? 0 : 1;
                    int row = (int)((downY - 92) / 76);
                    String[] actions = {"wifi", "bluetooth", "data", "location", "rotation", "flash"};
                    int idx = row * 2 + col;
                    if (idx >= 0 && idx < actions.length) openAction(actions[idx]);
                    return true;
                }

                return true;
            }
            return true;
        }

        class HandlerClock {
            final android.os.Handler h = new android.os.Handler();
            final Runnable r = new Runnable() { public void run() { refreshClock(); h.postDelayed(this, 1000); } };
            void start() { r.run(); }
        }
    }
}
