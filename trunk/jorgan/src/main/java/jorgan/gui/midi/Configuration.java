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
package jorgan.gui.midi;

import java.util.*;
import java.awt.*;
import java.util.prefs.*;

import jorgan.config.prefs.*;

/**
 * Configuration of the swing package.
 */
public class Configuration extends PreferencesConfiguration {

  private static final int     MIDI_LOG_MAX = 500;
  private static final boolean MIDI_LOG_HEX = false;
  
  private static Configuration sharedInstance = new Configuration(true);

  private int       midiLogMax;
  private boolean   midiLogHex;

  private Configuration(boolean sharedFlag) {
  }

  public Configuration() {
  }

  protected void restore(Preferences prefs) {
    midiLogMax = prefs.getInt    ("midiLogMax", MIDI_LOG_MAX);
    midiLogHex = prefs.getBoolean("midiLogHex", MIDI_LOG_HEX);
  }

  protected void backup(Preferences prefs) {
    prefs.putInt    (       "midiLogMax"      , midiLogMax);
    prefs.putBoolean(       "midiLogHex"      , midiLogHex);
  }

  public int getMidiLogMax() {
    return midiLogMax;
  }
  
  public boolean getMidiLogHex() {
    return midiLogHex;
  }
  
  public void setMidiLogMax(int midiLogMax) {
    this.midiLogMax = midiLogMax;
    
    fireConfigurationChanged();
  }
  
  public void setMidiLogHex(boolean midiLogHex){
    this.midiLogHex = midiLogHex;
    
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

  public static Point getPoint(Preferences prefs, String key, Point def) {
    String point = prefs.get(key, null);
    if (point != null) {
      try {
        StringTokenizer tokens = new StringTokenizer(point, ",");

        int x = Integer.parseInt(tokens.nextToken().trim());
        int y = Integer.parseInt(tokens.nextToken().trim());

        return new Point(x, y);
      } catch (Exception ex) {
        // fall through
      }
    }
    return def;
  }

  public static void putPoint(Preferences prefs, String key, Point point) {
    if (point == null) {
      prefs.remove(key);
    } else {
      prefs.put(key, point.x + ", " + point.y);
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