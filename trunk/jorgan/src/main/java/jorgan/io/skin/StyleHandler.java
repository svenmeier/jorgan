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

public class StyleHandler extends Handler {

  private Style style;

  /**
   * Constructor.
   */
  public StyleHandler(AbstractReader reader) {
    super(reader);

    style = new Style();
  }
  
  public StyleHandler(AbstractWriter writer, String tag, Style style) {
    super(writer, tag);
    
    this.style = style;
  }

  public Style getStyle() {
    return style;
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("name".equals(qName)) {
      new StringHandler(getReader()) {
        public void finished() {
          style.setName(getString());
        }
      };
    } else if ("description".equals(qName)) {
      new StringHandler(getReader()) {
        public void finished() {
          style.setDescription(getString());
        }
      };
    } else if ("state".equals(qName)) {
      new StateHandler(getReader()) {
        public void finished() {
          style.addState(getState());
        }
      };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  public void children() throws IOException {
    super.children();

    new StringHandler(getWriter(), "name", style.getName()).start();
    if (style.getDescription() != null) {
      new StringHandler(getWriter(), "description", style.getDescription()).start();
    }
    for (int s = 0; s < style.getStateCount(); s++) {
      new StateHandler(getWriter(), "state", style.getState(s)).start();
    }
  }
}
