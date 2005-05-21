/*
 * jOrgan - Java Virtual Pipe Organ
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
package jorgan.shell;

import java.util.prefs.*;

import jorgan.config.prefs.*;

/**
 * Configuration of the shell package.
 */
public class Configuration extends PreferencesConfiguration {

  private static final boolean USE_ENCODING = true;
  private static final String  ENCODING     = "";

  private static Configuration sharedInstance = new Configuration();

  private boolean useDefaultEncoding;
  private String  encoding;

  protected void restore(Preferences prefs) {
    useDefaultEncoding = getBoolean(prefs, "useDefaultEncoding", USE_ENCODING);
    encoding           = get       (prefs, "encoding"          , ENCODING);
  }

  protected void backup(Preferences prefs) {
    putBoolean(prefs, "useDefaultEncoding", useDefaultEncoding);
    put       (prefs, "encoding", encoding);
  }

  public boolean getUseDefaultEncoding() {
    return useDefaultEncoding;
  }

  public void setUseDefaultEncoding(boolean useDefaultEncoding) {
    this.useDefaultEncoding = useDefaultEncoding;
    
    fireConfigurationChanged();
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
    
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