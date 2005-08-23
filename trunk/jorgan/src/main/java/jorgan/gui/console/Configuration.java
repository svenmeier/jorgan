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
  private static final Font    COMBINATION_FONT= new Font("Arial", Font.PLAIN , 12);
  private static final Font    SEQUENCE_FONT   = new Font("Arial", Font.PLAIN , 12);
  private static final Font    SWELL_FONT      = new Font("Arial", Font.PLAIN , 12);
  private static final Font    CRESCENDO_FONT  = new Font("Arial", Font.PLAIN , 12);
  private static final Font    TREMULANT_FONT  = new Font("Arial", Font.ITALIC, 12);
  private static final Font    VARIATION_FONT  = new Font("Arial", Font.ITALIC, 12);
  private static final Font    ACTIVATOR_FONT  = new Font("Arial", Font.PLAIN , 12);
  
  private static Configuration sharedInstance = new Configuration();

  private boolean interpolate;
  private boolean showShortcut;  
  private Color   shortcutColor;
  private Font    shortcutFont;
  private Font    labelFont;
  private Font    stopFont;
  private Font    couplerFont;
  private Font    combinationFont;
  private Font    sequenceFont;
  private Font    swellFont;
  private Font    crescendoFont;
  private Font    tremulantFont;
  private Font    variationFont;
  private Font    activatorFont;

  protected void restore(Preferences prefs) {
    interpolate    = getBoolean(prefs, "interpolate", INTERPOLATE);

    showShortcut   = getBoolean(prefs, "showShortcut", SHOW_SHORTCUT);
    shortcutColor  = getColor  (prefs, "shortcutColor", SHORTCUT_COLOR);
    shortcutFont   = getFont   (prefs, "shortcutFont"   , SHORTCUT_FONT);

    labelFont      = getFont   (prefs, "labelFont"      , LABEL_FONT);
    stopFont       = getFont   (prefs, "stopFont"       , STOP_FONT);
    couplerFont    = getFont   (prefs, "couplerFont"    , COUPLER_FONT);
    combinationFont= getFont   (prefs, "combinationFont", COMBINATION_FONT);
    sequenceFont   = getFont   (prefs, "sequenceFont"   , SEQUENCE_FONT);
    swellFont      = getFont   (prefs, "swellFont"      , SWELL_FONT);
    crescendoFont  = getFont   (prefs, "crescendoFont"  , CRESCENDO_FONT);
    tremulantFont  = getFont   (prefs, "tremulantFont"  , TREMULANT_FONT);
    variationFont  = getFont   (prefs, "variationFont"  , VARIATION_FONT);
    activatorFont  = getFont   (prefs, "activatorFont"  , ACTIVATOR_FONT);
  }

  protected void backup(Preferences prefs) {
    putBoolean(prefs, "interpolate", interpolate);

    putBoolean(prefs, "showShortcut", showShortcut);
    putColor  (prefs, "shortcutColor", shortcutColor);
    putFont   (prefs, "shortcutFont" , shortcutFont);

    putFont   (prefs, "labelFont"     , labelFont);
    putFont   (prefs, "stopFont"      , stopFont);
    putFont   (prefs, "couplerFont"   , couplerFont);
    putFont   (prefs, "combinationFont", combinationFont);
    putFont   (prefs, "sequenceFont"  , sequenceFont);
    putFont   (prefs, "swellFont"     , swellFont);
    putFont   (prefs, "crescendoFont" , crescendoFont);
    putFont   (prefs, "tremulantFont" , tremulantFont);
    putFont   (prefs, "variationFont" , variationFont);
    putFont   (prefs, "activatorFont" , activatorFont);
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

  public Font getCombinationFont() {
    return combinationFont;
  }

  public Font getSequenceFont() {
      return sequenceFont;
    }

  public Font getSwellFont() {
    return swellFont;
  }

  public Font getCrescendoFont() {
      return crescendoFont;
    }

  public Font getTremulantFont() {
    return tremulantFont;
  }

  public Font getVariationFont() {
    return variationFont;
  }

  public Font getActivatorFont() {
    return activatorFont;
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

  public void setCombinationFont(Font combinationFont) {
    this.combinationFont = combinationFont;
    
    fireConfigurationChanged();
  }

  public void setSequenceFont(Font sequenceFont) {
    this.sequenceFont = sequenceFont;
      
    fireConfigurationChanged();
  }

  public void setSwellFont(Font swellFont) {
    this.swellFont = swellFont;

    fireConfigurationChanged();
  }

  public void setCrescendoFont(Font crescendoFont) {
    this.crescendoFont = crescendoFont;
        
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

  public void setActivatorFont(Font activatorFont) {
    this.activatorFont = activatorFont;
      
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