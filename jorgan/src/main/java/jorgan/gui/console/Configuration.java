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

import java.util.*;
import java.awt.*;
import java.util.prefs.*;

import jorgan.config.prefs.*;

/**
 * Configuration of the view package.
 */
public class Configuration extends PreferencesConfiguration {

  private static final boolean INTERPOLATE     = false;
  private static final boolean SHOW_SHORTCUT   = true;  
  private static final Color   SHORTCUT_COLOR  = Color.blue;
  private static final Font    SHORTCUT_FONT   = new Font("Arial", Font.PLAIN , 10);
  private static final Font    LABEL_FONT      = new Font("Arial", Font.BOLD  , 14);
  private static final Font    STOP_FONT       = new Font("Arial", Font.PLAIN , 12);
  private static final Font    COUPLER_FONT    = new Font("Arial", Font.ITALIC, 12);
  private static final Font    PISTON_FONT     = new Font("Arial", Font.PLAIN , 12);
  private static final Font    SWELL_FONT      = new Font("Arial", Font.PLAIN , 12);
  private static final Font    TREMULANT_FONT  = new Font("Arial", Font.ITALIC, 12);
  private static final Font    VARIATION_FONT  = new Font("Arial", Font.ITALIC, 12);
  
  private static Configuration sharedInstance = new Configuration();

  private boolean interpolate;
  private boolean showShortcut;  
  private Color   shortcutColor;
  private Font    shortcutFont;
  private Font    labelFont;
  private Font    stopFont;
  private Font    couplerFont;
  private Font    pistonFont;
  private Font    swellFont;
  private Font    tremulantFont;
  private Font    variationFont;

  protected void restore(Preferences prefs) {
    interpolate    = prefs.getBoolean("interpolate", INTERPOLATE);

    showShortcut   = prefs.getBoolean("showShortcut", SHOW_SHORTCUT);
    shortcutColor  = getColor(prefs, "shortcutColor", SHORTCUT_COLOR);
    shortcutFont   = getFont (prefs, "shortcutFont"   , SHORTCUT_FONT);

    labelFont      = getFont(prefs, "labelFont"      , LABEL_FONT);
    stopFont       = getFont(prefs, "stopFont"       , STOP_FONT);
    couplerFont    = getFont(prefs, "couplerFont"    , COUPLER_FONT);
    pistonFont     = getFont(prefs, "pistonFont"     , PISTON_FONT);
    swellFont      = getFont(prefs, "swellFont"      , SWELL_FONT);
    tremulantFont  = getFont(prefs, "tremulantFont"  , TREMULANT_FONT);
    variationFont  = getFont(prefs, "variationFont"  , VARIATION_FONT);
  }

  protected void backup(Preferences prefs) {
    prefs.putBoolean("interpolate", interpolate);

    prefs.putBoolean("showShortcut", showShortcut);
    putColor(prefs, "shortcutColor", shortcutColor);
    putFont (prefs, "shortcutFont" , shortcutFont);

    putFont(prefs, "labelFont"     , labelFont);
    putFont(prefs, "stopFont"      , stopFont);
    putFont(prefs, "couplerFont"   , couplerFont);
    putFont(prefs, "pistonFont"    , pistonFont);
    putFont(prefs, "swellFont"     , swellFont);
    putFont(prefs, "tremulantFont" , tremulantFont);
    putFont(prefs, "variationFont" , variationFont);
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

  public Font getLabelFont() {
    return labelFont;
  }

  public Font getStopFont() {
    return stopFont;
  }

  public Font getCouplerFont() {
    return couplerFont;
  }

  public Font getPistonFont() {
    return pistonFont;
  }

  public Font getSwellFont() {
    return swellFont;
  }

  public Font getTremulantFont() {
    return tremulantFont;
  }

  public Font getVariationFont() {
    return variationFont;
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

  public void setLabelFont(Font labelFont) {
    this.labelFont = labelFont;
    
    fireConfigurationChanged();
  }

  public void setStopFont(Font stopFont) {
    this.stopFont = stopFont;
    
    fireConfigurationChanged();
  }

  public void setCouplerFont(Font couplerFont) {
    this.couplerFont = couplerFont;
    
    fireConfigurationChanged();
  }

  public void setPistonFont(Font pistonFont) {
    this.pistonFont = pistonFont;
    
    fireConfigurationChanged();
  }

  public void setSwellFont(Font swellFont) {
    this.swellFont = swellFont;

    fireConfigurationChanged();
  }

  public void setTremulantFont(Font tremulantFont) {
    this.tremulantFont = tremulantFont;
    
    fireConfigurationChanged();
  }

  public void setVariationFont(Font variationFont) {
    this.variationFont = variationFont;
    
    fireConfigurationChanged();
  }

  public static Font getFont(Preferences prefs, String key, Font def) {
    String font = prefs.get(key, null);
    if (font != null) {
      try {
        StringTokenizer tokens = new StringTokenizer(font, ",");
  
        String name  = tokens.nextToken().trim();
        int    style = Integer.parseInt(tokens.nextToken().trim());
        int    size  = Integer.parseInt(tokens.nextToken().trim());
  
        return new Font(name, style, size);
      } catch (Exception ex) {
        // fall through
      }
    }
    return def;
  }

  public static void putFont(Preferences prefs, String key, Font font) {
    if (font == null) {
      prefs.remove(key);
    } else {
      prefs.put(key, font.getName() + ", " + font.getStyle() + ", " + font.getSize());
    }
  }

  public static Color getColor(Preferences prefs, String key, Color def) {
    String color = prefs.get(key, null);
    if (color != null) {
      try {
        StringTokenizer tokens = new StringTokenizer(color, ",");

        int r = Integer.parseInt(tokens.nextToken().trim());
        int g = Integer.parseInt(tokens.nextToken().trim());
        int b = Integer.parseInt(tokens.nextToken().trim());

        return new Color(r, g, b);
      } catch (Exception ex) {
        // fall through
      }
    }
    return def;
  }

  public static void putColor(Preferences prefs, String key, Color color) {
    if (color == null) {
      prefs.remove(key);
    } else {
      prefs.put(key, color.getRed() + ", " + color.getGreen() + ", " + color.getBlue());
    }
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