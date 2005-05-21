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
package jorgan.config.prefs;

import java.util.prefs.*;

import jorgan.config.AbstractConfiguration;

/**
 * Abstract base class for configurations that can backup to / restore from
 * {@link java.util.prefs.Preferences}.
 */
public abstract class PreferencesConfiguration extends AbstractConfiguration {

  public PreferencesConfiguration() {

    restore(Preferences.userNodeForPackage(getClass()));
  }

  /**
   * Backup this configuration and all its children.
   */
  public final void backup() {
    fireConfigurationBackup();

    backup(Preferences.userNodeForPackage(getClass()));

    for (int c = 0; c < getChildCount(); c++) {
      getChild(c).backup();
    }
  }

  /**
   * Backup the values of this configuration.
   * <br>
   * This default implementation does nothing and should
   * be overridden by subclasses.
   *
   * @param preferences    preferences to backup to
   */
  protected void backup(Preferences preferences) {
  }

  /**
   * Restore this configuration and all its children.
   */
  public final void restore() {
    restore(Preferences.userNodeForPackage(getClass()));

    for (int c = 0; c < getChildCount(); c++) {
      getChild(c).restore();
    }

    fireConfigurationChanged();
  }

  /**
   * Restore the values of this configuration.
   * <br>
   * This default implementation does nothing and should
   * be overridden by subclasses.
   *
   * @param preferences    preferences to restore from
   */
  protected void restore(Preferences preferences) {
  }

  /**
   * Reset this configuration.
   * <br>
   * Children of this configuration are not changed.
   */
  public void reset() {
    restore(new ResetPreferences());
  }
  
  protected static void put(Preferences prefs, String key, String value) {
    if (value == null) {
      prefs.remove(key);
    } else {
      prefs.put(key, value);
    }
  }

  protected static void putInt(Preferences prefs, String key, int value) {
    prefs.putInt(key, value);
  }

  protected static void putBoolean(Preferences prefs, String key, boolean value) {
    prefs.putBoolean(key, value);
  }
  
  protected static String get(Preferences prefs, String key, String defaultValue) {
    return prefs.get(key, defaultValue);
  }

  protected static int getInt(Preferences prefs, String key, int defaultValue) {
    return prefs.getInt(key, defaultValue);
  }

  protected static boolean getBoolean(Preferences prefs, String key, boolean defaultValue) {
    return prefs.getBoolean(key, defaultValue);
  }
}