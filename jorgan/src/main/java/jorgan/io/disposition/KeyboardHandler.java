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

public class KeyboardHandler extends ElementHandler {

  private Keyboard keyboard;

  /**
   * Constructor.
   */
  public KeyboardHandler(AbstractReader reader, Attributes attributes) {
    super(reader, attributes);

    keyboard = new Keyboard();
  }
  
  public KeyboardHandler(AbstractWriter writer, String tag, Keyboard keyboard) {
    super(writer, tag);
    
    this.keyboard = keyboard;
  }

  public Keyboard getKeyboard() {
    return keyboard;
  }

  protected Element getElement() {
    return getKeyboard();
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("from".equals(qName)) {
      new KeyHandler(getReader()) {
        public void finished() {
          keyboard.setFrom(getKey());
        }
      };
    } else if ("to".equals(qName)) {
      new KeyHandler(getReader()) {
        public void finished() {
          keyboard.setTo(getKey());
        }
      };
    } else if ("transpose".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          keyboard.setTranspose(getInteger());
        }
      };
    } else if ("channel".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          keyboard.setChannel(getInteger());
        }
      };
    } else if ("command".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          keyboard.setCommand(getInteger());
        }
      };
    } else if ("threshold".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          keyboard.setThreshold(getInteger());
        }
      };
    } else if ("device".equals(qName)) {
      new StringHandler(getReader()) {
        public void finished() {
          keyboard.setDevice(getString());
        }
      };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  public void children() throws IOException {
    super.children();

    new IntegerHandler(getWriter(), "channel"  , keyboard.getChannel()).start();
    new IntegerHandler(getWriter(), "command"  , keyboard.getCommand()).start();
    new IntegerHandler(getWriter(), "threshold", keyboard.getThreshold()).start();
    new IntegerHandler(getWriter(), "transpose", keyboard.getTranspose()).start();

    if (keyboard.getFrom() != null) {
      new KeyHandler(getWriter(), "from", keyboard.getFrom()).start();
    }
    if (keyboard.getTo() != null) {
      new KeyHandler(getWriter(), "to", keyboard.getTo()).start();
    }
    if (keyboard.getDevice() != null) {
      new StringHandler(getWriter(), "device", keyboard.getDevice()).start();
    }
  }
}
