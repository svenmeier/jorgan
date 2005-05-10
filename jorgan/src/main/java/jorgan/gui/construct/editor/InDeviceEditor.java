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

import java.beans.*;

import jorgan.sound.midi.*;

/**
 * Property editor for a input device property.
 */
public class InDeviceEditor extends PropertyEditorSupport {

  private String[] tags;

  public InDeviceEditor() {
    String[] deviceNames = DevicePool.getMidiDeviceNames(false);

    tags = new String[1 + deviceNames.length];

    System.arraycopy(deviceNames, 0, tags, 1, deviceNames.length);
  }

  public String[] getTags() {

    return tags;
  }

  public String getAsText() {

    return (String)getValue();
  }

  public void setAsText(String string) {

    setValue(string);
  }
}
