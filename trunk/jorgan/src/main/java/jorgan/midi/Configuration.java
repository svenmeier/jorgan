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
package jorgan.midi;

import java.util.prefs.*;

import jorgan.config.prefs.*;

public class Configuration extends PreferencesConfiguration {

  private static final boolean SEND_ALL_NOTES_OFF = false;

  private static Configuration sharedInstance = new Configuration(true);

  private boolean sendAllNotesOff;
  
  private Configuration(boolean sharedFlag) {
    addChild(jorgan.midi.merge.Configuration.instance());
    addChild(jorgan.midi.log.Configuration.instance());
  }

  public Configuration() {
    addChild(new jorgan.midi.merge.Configuration());
    addChild(new jorgan.midi.log.Configuration());
  }
  
  protected void restore(Preferences prefs) {
    sendAllNotesOff = getBoolean(prefs, "sendAllNotesOff", SEND_ALL_NOTES_OFF);
  }

  protected void backup(Preferences prefs) {
    putBoolean(prefs, "sendAllNotesOff", sendAllNotesOff);
  }

  public boolean getSendAllNotesOff() {
      return sendAllNotesOff;
    }

  public void setSendAllNotesOff(boolean sendAllNotesOff) {
    this.sendAllNotesOff = sendAllNotesOff;
    
    fireConfigurationChanged();
  }

  public static Configuration instance() {
    return sharedInstance;
  }
}