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

public class StateHandler extends Handler {

  private State state;

  /**
   * Constructor.
   */
  public StateHandler(AbstractReader reader) {
    super(reader);

    state = new State();
  }
  
  public StateHandler(AbstractWriter writer, String tag, State state) {
    super(writer, tag);
    
    this.state = state;
  }

  public State getState() {
    return state;
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("image".equals(qName)) {
      new ImageHandler(getReader()) {
        public void finished() {
          state.setImage(getImage());
        }
      };
    } else if ("label".equals(qName)) {
      new LabelHandler(getReader()) {
        public void finished() {
          state.setLabel(getLabel());
        }
      };
    } else if ("mouse".equals(qName)) {
      new MouseHandler(getReader()) {
        public void finished() {
          state.setMouse(getMouse());
        }
      };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  public void children() throws IOException {
    super.children();

    new ImageHandler(getWriter(), "image", state.getImage()).start();
    new LabelHandler(getWriter(), "label", state.getLabel()).start();
    new MouseHandler(getWriter(), "mouse", state.getMouse()).start();
  }
}
