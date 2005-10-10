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

import org.xml.sax.*;

import jorgan.skin.*;
import jorgan.xml.*;
import jorgan.xml.handler.*;

public class LabelHandler extends Handler {

  private Label label;

  /**
   * Constructor.
   */
  public LabelHandler(AbstractReader reader) {
    super(reader);

    label = new Label();
  }
  
  public LabelHandler(AbstractWriter writer, String tag, Label label) {
    super(writer, tag);
    
    this.label = label;
  }

  public Label getLabel() {
    return label;
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("color".equals(qName)) {
      new ColorHandler(getReader()) {
        public void finished() {
          label.setColor(getColor());
        }
      };
    } else if ("font".equals(qName)) {
      new FontHandler(getReader()) {
        public void finished() {
          label.setFont(getFont());
        }
      };
    } else if ("bounds".equals(qName)) {
      new RectangleHandler(getReader()) {
        public void finished() {
          label.setBounds(getRectangle());
        }
      };
    } else if ("rotation".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          label.setRotation(getInteger());
        }
      };
    } else if ("antialiased".equals(qName)) {
      new BooleanHandler(getReader()) {
        public void finished() {
          label.setAntialiased(getBoolean());
        }
      };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  public void children() throws IOException {
    super.children();

    new ColorHandler    (getWriter(), "color"      , label.getColor     ()).start();
    new FontHandler     (getWriter(), "font"       , label.getFont      ()).start();
    new RectangleHandler(getWriter(), "bounds"     , label.getBounds    ()).start();
    new IntegerHandler  (getWriter(), "rotation"   , label.getRotation  ()).start();
    new BooleanHandler  (getWriter(), "antialiased", label.isAntialiased()).start();
  }
}
