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
package jorgan.gui.construct.editor;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.beans.*;
import java.util.ResourceBundle;

/**
 * Property editor for a screen property.
 */
public class ScreenEditor extends PropertyEditorSupport {

  /**
   * The resource bundle.
   */
  protected static ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

  private String[] tags;
  
  private String defaultTag;

  public ScreenEditor() {
      
    defaultTag = resources.getString("construct.editor.screen.default");

    GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();    
    GraphicsDevice[] devices = environment.getScreenDevices();
    
    tags = new String[devices.length + 2];
    tags[0] = null;
    tags[1] = defaultTag;
    for (int d = 0; d < devices.length; d++) {
      tags[d + 2] = devices[d].getIDstring();
    }
  }

  public String[] getTags() {

    return tags;
  }

  public String getAsText() {

    String value = (String)getValue();
    if ("".equals(value)) {
        return defaultTag;
    } else {
        return value;
    }
  }

  public void setAsText(String string) {

    if (defaultTag.equals(string)) {
        setValue("");
    } else {
        setValue(string);
    }
  }
}
