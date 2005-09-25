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
  private static final Rectangle FRAME_BOUNDS             = null;
  private static final int       FRAME_STATE              = JFrame.NORMAL;
  private static final String    PLAY_DOCKING             = null;  
  private static final String    CONSTRUCT_DOCKING        = null;  
  
  private static Configuration sharedInstance = new Configuration(true);

  private boolean   useSystemLookAndFeel;
  private boolean   showAboutOnStartup;
  private Rectangle frameBounds;
  private int       frameState;
  private String    playDocking;
  private String    constructDocking;

  private Configuration(boolean sharedFlag) {
    addChild(jorgan.gui.console.Configuration.instance());
    addChild(jorgan.gui.construct.Configuration.instance());
  }

  public Configuration() {
    addChild(new jorgan.gui.console.Configuration());
    addChild(new jorgan.gui.construct.Configuration());
  }

  protected void restore(Preferences prefs) {
    useSystemLookAndFeel        = getBoolean  (prefs, "useSystemLookAndFeel", USE_SYSTEM_LOOK_AND_FEEL);
    showAboutOnStartup          = getBoolean  (prefs, "showAboutOnStartup"  , SHOW_ABOUT_ON_STARTUP);
    frameBounds                 = getRectangle(prefs, "frameBounds"         , FRAME_BOUNDS);
    frameState                  = getInt      (prefs, "frameState"          , FRAME_STATE);
    playDocking                 = get         (prefs, "playDocking"         , PLAY_DOCKING);
    constructDocking            = get         (prefs, "constructDocking"    , CONSTRUCT_DOCKING);
  }

  protected void backup(Preferences prefs) {
    putBoolean  (prefs, "useSystemLookAndFeel", useSystemLookAndFeel);
    putBoolean  (prefs, "showAboutOnStartup"  , showAboutOnStartup);
    putRectangle(prefs, "frameBounds"         , frameBounds);
    putInt      (prefs, "frameState"          , frameState);
    put         (prefs, "playDocking"         , playDocking);
    put         (prefs, "constructDocking"    , constructDocking);
  }

  public boolean getUseSystemLookAndFeel() {
    return useSystemLookAndFeel;
  }

  public boolean getShowAboutOnStartup() {
    return showAboutOnStartup;
  }

  public Rectangle getFrameBounds() {
    return frameBounds;
  }

  public int getFrameState() {
    return frameState;
  }

  public String getPlayDocking() {
    return playDocking;
  }

  public String getConstructDocking() {
    return constructDocking;
  }

  public void setFrameBounds(Rectangle rectangle) {
    this.frameBounds = rectangle;
    
    fireConfigurationChanged();
  }

  public void setFrameState(int frameState) {
    this.frameState = frameState;
    
    fireConfigurationChanged();
  }

  public void setPlayDocking(String docking) {
    this.playDocking = docking;
    
    fireConfigurationChanged();
  }

  public void setConstructDocking(String docking) {
      this.constructDocking = docking;
      
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

  /**
   * Get the shared configuration.
   *
   * @return configuration
   */
  public static Configuration instance() {
    return sharedInstance;
  }
}