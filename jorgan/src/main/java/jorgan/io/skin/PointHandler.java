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
package jorgan.io.skin;

import java.io.*;
import java.awt.Point;

import org.xml.sax.*;

import jorgan.xml.*;
import jorgan.xml.handler.*;

public class PointHandler extends Handler {

  private int x;
  private int y;

  private Point point;

  public PointHandler(AbstractWriter writer, String tag, Point point) {
    super(writer, tag);

    this.point = point;
  }

  public PointHandler(AbstractReader reader) {
    super(reader);
  }

  public Point getPoint() {
    return point;
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
    point = new Point(x, y);
    
    finished();
  }
  
  public void children() throws IOException {
    new IntegerHandler(getWriter(), "x", point.x).start();
    new IntegerHandler(getWriter(), "y", point.y).start();
  }
}
