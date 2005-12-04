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
package jorgan.xml.handler;

// jorgan
import java.io.IOException;

import jorgan.xml.*;

/**
 * A handler for boolean values.
 */
public class BooleanHandler extends Handler {

  private boolean bool = true;
  
  /**
   * Constructor.
   */
  public BooleanHandler(AbstractReader reader) {
    super(reader);
  }

  public BooleanHandler(AbstractWriter writer, String tag, boolean bool) {
    super(writer, tag);
    
    this.bool = bool;
  }
  
  public boolean getBoolean() {
    return bool;
  }

  public void characters(XMLWriter writer) throws IOException {

    writer.characters(Boolean.toString(bool));
  }
    
  protected void finish() {
    if (getCharacters().length() == 0) {
      bool = true;
    } else {
      bool = Boolean.valueOf(getCharacters()).booleanValue();
    }
    finished();
  }
}