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
package jorgan.io.disposition;

import java.io.*;

import org.xml.sax.*;

import jorgan.disposition.*;
import jorgan.xml.*;
import jorgan.xml.handler.*;

public class LocationHandler extends Handler {

  private int x = 0;
  private int y = 0;

  private Location location;

  public LocationHandler(AbstractWriter writer, String tag, Location location) {
    super(writer, tag);

    this.location = location;
  }

  public LocationHandler(AbstractReader reader) {
    super(reader);
  }

  public Location getLocation() {
    return location;
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("x".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          x = getInteger();
        }
      };
    } else if ("y".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          y = getInteger();
        }
      };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  protected void finish() {
    location = new Location(x, y);
    
    finished();
  }
  
  public void children() throws IOException {
    new IntegerHandler(getWriter(), "x", location.getX()).start();
    new IntegerHandler(getWriter(), "y", location.getY()).start();
  }
}
