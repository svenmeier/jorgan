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
import java.awt.Rectangle;

import org.xml.sax.*;

import jorgan.xml.*;
import jorgan.xml.handler.*;

public class RectangleHandler extends Handler {

  private int x;
  private int y;
  private int width;
  private int height;

  private Rectangle rectangle;

  public RectangleHandler(AbstractWriter writer, String tag, Rectangle rectangle) {
    super(writer, tag);

    this.rectangle = rectangle;
  }

  public RectangleHandler(AbstractReader reader) {
    super(reader);
  }

  public Rectangle getRectangle() {
    return rectangle;
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
    } else if ("width".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          width = getInteger();
        }
      };
    } else if ("height".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          height = getInteger();
        }
      };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  protected void finish() {
    rectangle = new Rectangle(x, y, width, height);
    
    finished();
  }
  
  public void children() throws IOException {
    new IntegerHandler(getWriter(), "x"     , rectangle.x     ).start();
    new IntegerHandler(getWriter(), "y"     , rectangle.y     ).start();
    new IntegerHandler(getWriter(), "width" , rectangle.width ).start();
    new IntegerHandler(getWriter(), "height", rectangle.height).start();
  }
}
