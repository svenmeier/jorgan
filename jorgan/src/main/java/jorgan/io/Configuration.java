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
package jorgan.io;

import java.io.*;
import java.util.*;
import java.util.prefs.*;

import jorgan.config.prefs.*;
import jorgan.util.Bootstrap;

/**
 * Configuration of the IO package.
 */
public class Configuration extends PreferencesConfiguration {

  public static final int REGISTRATION_CHANGES_CONFIRM = 0; 
  public static final int REGISTRATION_CHANGES_SAVE    = 1;
  public static final int REGISTRATION_CHANGES_IGNORE  = 2; 
  
  private static final boolean RECENT_OPEN_ON_STARTUP = false;
  private static final int     RECENT_MAX = 4;
  private static final int     REGISTRATION_CHANGES = REGISTRATION_CHANGES_CONFIRM;

  private static Configuration sharedInstance = new Configuration();

  private boolean recentOpenOnStartup;
  private int     recentMax;
  private File    recentDirectory;
  private List    recentFiles;
  private int     registrationChanges;

  protected void restore(Preferences prefs) {
    registrationChanges = getInt    (prefs, "registrationChanges", REGISTRATION_CHANGES);
    recentOpenOnStartup = getBoolean(prefs, "recentOpenOnStartup", RECENT_OPEN_ON_STARTUP);
    recentMax           = getInt    (prefs, "recentMax"          , RECENT_MAX);
    recentDirectory     = getFile   (prefs,   "recentDirectory"    , RECENT_DIRECTORY());
    recentFiles = new ArrayList();
    for (int r = 0; ; r++) {
      File def = (r == 0) ? RECENT_FILE() : null;
      File recentFile = getFile(prefs, "recentFiles[" + r + "]", def);
      if (recentFile == null) {
        break;
      } else {
        recentFiles.add(recentFile);
      }
    }
  }

  protected void backup(Preferences prefs) {
    putInt    (prefs, "registrationChanges", registrationChanges);
    putBoolean(prefs, "recentOpenOnStartup", recentOpenOnStartup);
    putInt    (prefs, "recentMax"          , recentMax);
    putFile   (prefs,   "recentDirectory"    , recentDirectory);
    for (int r = 0; ; r++) {
      String key = "recentFiles[" + r + "]";
      if (prefs.get(key, null) == null) {
        break ;
      }
      prefs.remove(key);
    }
    for (int r = 0; r < recentFiles.size(); r++) {
      putFile(prefs, "recentFiles[" + r + "]", (File)recentFiles.get(r));
    }
  }

  public int getRegistrationChanges() {
    return registrationChanges;
  }

  public void setRegistrationChanges(int registrationChanges) {
    if (registrationChanges < REGISTRATION_CHANGES_CONFIRM && registrationChanges > REGISTRATION_CHANGES_CONFIRM) {
        throw new IllegalArgumentException("unknown registration change '" + registrationChanges + "'");
    }
    this.registrationChanges = registrationChanges;
    
    fireConfigurationChanged();
  }

  public boolean getRecentOpenOnStartup() {
    return recentOpenOnStartup;
  }

  public void setRecentOpenOnStartup(boolean recentOpenOnStartup) {
    this.recentOpenOnStartup = recentOpenOnStartup;
    
    fireConfigurationChanged();
  }

  public File getRecentDirectory() {
    return recentDirectory;
  }

  public List getRecentFiles() {
    return Collections.unmodifiableList(recentFiles);
  }

  public File getRecentFile() {
    if (recentFiles.size() > 0) {
      return (File)recentFiles.get(0);
    }
    return null;
  }

  public int getRecentMax() {
    return recentMax;
  }

  public void setRecentMax(int recentMax) {
    this.recentMax = recentMax;

    for (int r = recentFiles.size() - 1; r >= recentMax; r--) {
      recentFiles.remove(r);
    }
    
    fireConfigurationChanged();
  }

  public void addRecentFile(File file) {
    try {
      File canonical = file.getCanonicalFile();

      int index = recentFiles.indexOf(canonical);
      if (index == -1) {
        recentFiles.add(0, canonical);
        if (recentFiles.size() > recentMax) {
          recentFiles.remove(recentMax);
        }
      } else {
        recentFiles.remove(index);
        recentFiles.add(0, canonical);
      }

      recentDirectory = canonical.getParentFile();
    } catch (IOException ex) {
      // ignoe
    }
    
    fireConfigurationChanged();
  }

  public void removeRecentFile(File file) {
    try {
      File canonical = file.getCanonicalFile();

      int index = recentFiles.indexOf(canonical);
      if (index != -1) {
        recentFiles.remove(index);
      }
    } catch (IOException ex) {
      // ignoe
    }
    
    fireConfigurationChanged();
  }

  protected static File getFile(Preferences prefs, String key, File def) {
    String file = prefs.get(key, null);
    if (file != null) {
      return new File(file);
    }
    return def;
  }

  protected static void putFile(Preferences prefs, String key, File file) {
    if (file == null) {
      prefs.remove(key);
    } else {
      prefs.put(key, file.getPath());
    }
  }

  protected File RECENT_FILE() {
    return new File(RECENT_DIRECTORY(), "Example.disposition");    
  }
  
  protected File RECENT_DIRECTORY() {
    return new File(Bootstrap.getDirectory(), "dispositions");    
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