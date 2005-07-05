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
package jorgan.midi.merge;

import java.util.*;
import java.util.prefs.*;

import jorgan.sound.midi.merge.*;

import jorgan.config.prefs.*;

public class Configuration extends PreferencesConfiguration {

  private static final Configuration instance = new Configuration();

  private List inputs;
  
  public Configuration() {
  }
  
  protected void restore(Preferences prefs) {
    inputs = new ArrayList();
    for (int i = 0; ; i++) {
      MergeInput input = getMergeInput(prefs, "input[" + i + "]", null);
      if (input == null) {
        break;
      } else {
        inputs.add(input);
      }
    }
  }

  protected void backup(Preferences prefs) {
    for (int i = 0; ; i++) {
      String key = "input[" + i + "]";
      if (prefs.get(key, null) == null) {
        break ;
      }
      prefs.remove(key);
    }
    for (int i = 0; i < inputs.size(); i++) {
      putMergeInput(prefs, "input[" + i + "]", (MergeInput)inputs.get(i));
    }
  }

  public List getInputs() {
    return Collections.unmodifiableList(inputs);
  }
  
  public void setInputs(List inputs) {
    this.inputs = new ArrayList(inputs);
    
    fireConfigurationChanged();
  }
  
  protected MergeInput getMergeInput(Preferences prefs, String key, MergeInput def) {
    String input = prefs.get(key, null);
    if (input != null) {
      try {
        int colon = input.indexOf(':');

        int    channel = Integer.parseInt(input.substring(0, colon));
        String device  = input.substring(colon + 1); 

        return new MergeInput(device, channel);
      } catch (Exception ex) {
        // use default
      }
    }
    return def;
  }

  protected void putMergeInput(Preferences prefs, String key, MergeInput input) {
    prefs.put(key, input.getChannel() + ":" + input.getDevice());
  }

  public static Configuration instance() {
    return instance;
  }
}