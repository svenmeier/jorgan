/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jorgan.gui.console;

import java.awt.Color;
import java.awt.Font;
import java.util.prefs.Preferences;

import jorgan.config.prefs.PreferencesConfiguration;

/**
 * Configuration of the view package.
 */
public class Configuration extends PreferencesConfiguration {

    private static final boolean INTERPOLATE = false;

    private static final boolean SHOW_SHORTCUT = true;

    private static final Color SHORTCUT_COLOR = Color.blue;

    private static final Font SHORTCUT_FONT = new Font("Arial", Font.PLAIN, 10);

    private static final Font FONT = new Font("Arial", Font.PLAIN, 12);

    private static Configuration sharedInstance = new Configuration();

    private boolean interpolate;

    private boolean showShortcut;

    private Color shortcutColor;

    private Font shortcutFont;

    private Font font;

    protected void restore(Preferences prefs) {
        interpolate = getBoolean(prefs, "interpolate", INTERPOLATE);

        showShortcut = getBoolean(prefs, "showShortcut", SHOW_SHORTCUT);
        shortcutColor = getColor(prefs, "shortcutColor", SHORTCUT_COLOR);
        shortcutFont = getFont(prefs, "shortcutFont", SHORTCUT_FONT);

        font = getFont(prefs, "font", FONT);
    }

    protected void backup(Preferences prefs) {
        putBoolean(prefs, "interpolate", interpolate);

        putBoolean(prefs, "showShortcut", showShortcut);
        putColor(prefs, "shortcutColor", shortcutColor);
        putFont(prefs, "shortcutFont", shortcutFont);

        putFont(prefs, "font", font);
    }

    public boolean getInterpolate() {
        return interpolate;
    }

    public boolean getShowShortcut() {
        return showShortcut;
    }

    public Color getShortcutColor() {
        return shortcutColor;
    }

    public Font getShortcutFont() {
        return shortcutFont;
    }

    public Font getFont() {
        return font;
    }

    public void setInterpolate(boolean interpolate) {
        this.interpolate = interpolate;

        fireConfigurationChanged();
    }

    public void setShowShortcut(boolean showShortcut) {
        this.showShortcut = showShortcut;

        fireConfigurationChanged();
    }

    public void setShortcutColor(Color shortcutColor) {
        this.shortcutColor = shortcutColor;

        fireConfigurationChanged();
    }

    public void setShortcutFont(Font shortcutFont) {
        this.shortcutFont = shortcutFont;

        fireConfigurationChanged();
    }

    public void setFont(Font font) {
        this.font = font;

        fireConfigurationChanged();
    }

    /**
     * Get the shared configuration.
     * 
     * @return configuration
     */
    public static Configuration instance() {
        return sharedInstance;
    }
}