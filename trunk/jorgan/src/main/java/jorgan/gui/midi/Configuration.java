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
    midiLogMax = getInt    (prefs, "midiLogMax", MIDI_LOG_MAX);
    midiLogHex = getBoolean(prefs, "midiLogHex", MIDI_LOG_HEX);
  }

  protected void backup(Preferences prefs) {
    putInt    (prefs, "midiLogMax", midiLogMax);
    putBoolean(prefs, "midiLogHex", midiLogHex);
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
  
  /**
   * Get the shared configuration.
   *
   * @return configuration
   */
  public static Configuration instance() {
    return sharedInstance;
  }
}