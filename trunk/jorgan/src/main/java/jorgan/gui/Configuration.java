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
package jorgan.gui;

import java.util.*;
import java.awt.*;
import java.util.prefs.*;
import javax.swing.*;

import jorgan.config.prefs.*;

/**
 * Configuration of the swing package.
 */
public class Configuration extends PreferencesConfiguration {

  private static final boolean   USE_SYSTEM_LOOK_AND_FEEL = true;
  private static final boolean   SHOW_ABOUT_ON_STARTUP    = true;
  private static final boolean   DISABLE_SCREENSAVER      = true;
  private static final Rectangle FRAME_BOUNDS             = null;
  private static final int       FRAME_STATE              = JFrame.NORMAL;
  private static final String    DOCKABLES                = "SLICE[SLICE[DOCK[false,!REFERENCES,!PROPERTIES],DOCK[false,!ELEMENTS],3,0.5],SLICE[DOCK[false,!KEYBOARD,!MIDI_LOG,!PROBLEMS],BRIDGE[!CONSOLES],3,0.25],2,0.25]";  
  
  private static Configuration sharedInstance = new Configuration(true);

  private boolean   useSystemLookAndFeel;
  private boolean   showAboutOnStartup;
  private boolean   disableScreenSaver;
  private Rectangle frameBounds;
  private int       frameState;
  private String    dockables;

  private Configuration(boolean sharedFlag) {
    addChild(jorgan.gui.console.Configuration.instance());
    addChild(jorgan.gui.construct.Configuration.instance());
    addChild(jorgan.gui.midi.Configuration.instance());
  }

  public Configuration() {
    addChild(new jorgan.gui.console.Configuration());
    addChild(new jorgan.gui.construct.Configuration());
    addChild(new jorgan.gui.midi.Configuration());
  }

  protected void restore(Preferences prefs) {
    useSystemLookAndFeel        = prefs.getBoolean(       "useSystemLookAndFeel", USE_SYSTEM_LOOK_AND_FEEL);
    showAboutOnStartup          = prefs.getBoolean(       "showAboutOnStartup"  , SHOW_ABOUT_ON_STARTUP);
    disableScreenSaver          = prefs.getBoolean(       "disableScreenSaver"  , DISABLE_SCREENSAVER);
    frameBounds                 = getRectangle    (prefs, "frameBounds"         , FRAME_BOUNDS);
    frameState                  = prefs.getInt    (       "frameState"          , FRAME_STATE);
    dockables                   = prefs.get       (       "dockables"           , DOCKABLES);
  }

  protected void backup(Preferences prefs) {
    prefs.putBoolean(       "useSystemLookAndFeel", useSystemLookAndFeel);
    prefs.putBoolean(       "showAboutOnStartup"  , showAboutOnStartup);
    prefs.putBoolean(       "disableScreenSaver"  , disableScreenSaver);
    putRectangle    (prefs, "frameBounds"         , frameBounds);
    prefs.putInt    (       "frameState"          , frameState);
    prefs.put       (       "dockables"           , dockables);
  }

  public boolean getUseSystemLookAndFeel() {
    return useSystemLookAndFeel;
  }

  public boolean getShowAboutOnStartup() {
    return showAboutOnStartup;
  }

  public boolean getDisableScreenSaver() {
    return disableScreenSaver;
  }

  public Rectangle getFrameBounds() {
    return frameBounds;
  }

  public int getFrameState() {
    return frameState;
  }

  public String getDockables() {
    return dockables;
  }

  public void setFrameBounds(Rectangle rectangle) {
    this.frameBounds = rectangle;
    
    fireConfigurationChanged();
  }

  public void setFrameState(int frameState) {
    this.frameState = frameState;
    
    fireConfigurationChanged();
  }

  public void setDockables(String dockables) {
    this.dockables = dockables;
    
    fireConfigurationChanged();
  }

  public void setUseSystemLookAndFeel(boolean useSystemLookAndFeel) {
    this.useSystemLookAndFeel = useSystemLookAndFeel;
    
    fireConfigurationChanged();
  }

  public void setShowAboutOnStartup(boolean showAboutOnStartup) {
    this.showAboutOnStartup = showAboutOnStartup;
    
    fireConfigurationChanged();
  }

  public void setDisableScreenSaver(boolean disableScreenSaver) {
    this.disableScreenSaver = disableScreenSaver;
    
    fireConfigurationChanged();
  }

  public static Rectangle getRectangle(Preferences prefs, String key, Rectangle def) {
    String rectangle = prefs.get(key, null);
    if (rectangle != null) {
      try {
        StringTokenizer tokens = new StringTokenizer(rectangle, ",");

        int x = Integer.parseInt(tokens.nextToken().trim());
        int y = Integer.parseInt(tokens.nextToken().trim());
        int w = Integer.parseInt(tokens.nextToken().trim());
        int h = Integer.parseInt(tokens.nextToken().trim());

        return new Rectangle(x, y, w, h);
      } catch (Exception ex) {
        // fall through
      }
    }
    return def;
  }

  public static void putRectangle(Preferences prefs, String key, Rectangle rectangle) {
    if (rectangle == null) {
      prefs.remove(key);
    } else {
      prefs.put(key, rectangle.x + ", " + rectangle.y + ", " + rectangle.width + ", " + rectangle.height);
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