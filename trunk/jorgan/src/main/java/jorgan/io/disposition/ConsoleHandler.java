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

public class ConsoleHandler extends ElementHandler {

  private Console console;

  /**
   * Constructor.
   */
  public ConsoleHandler(AbstractReader reader, Attributes attributes) {
    super(reader, attributes);
    
    console = new Console();
  }

  public ConsoleHandler(AbstractWriter writer, String tag, Console console) {
    super(writer, tag);
    
    this.console = console;
  }

  public Console getConsole() {
    return console;
  }
  
  protected Element getElement() {
    return console;
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("device".equals(qName)) {
      new StringHandler(getReader()) {
        public void finished() {
          console.setDevice(getString());
        }
      };
    } else if ("skin".equals(qName)) {
      new StringHandler(getReader()) {
        public void finished() {
          console.setSkin(getString());
        }
      };
    } else if ("zoom".equals(qName)) {
      new DoubleHandler(getReader()) {
        public void finished() {
          console.setZoom(getDouble());
        }
      };
    } else if ("screen".equals(qName)) {
      new StringHandler(getReader()) {
        public void finished() {
          console.setScreen(getString());
        }
      };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  public void children() throws IOException {
    super.children();

    if (console.getDevice() != null) {
      new StringHandler(getWriter(), "device", console.getDevice()).start();
    }
    if (console.getSkin() != null) {
      new StringHandler(getWriter(), "skin", console.getSkin()).start();
    }
    if (console.getScreen() != null) {
      new StringHandler(getWriter(), "screen", console.getScreen()).start();
    }
    new DoubleHandler(getWriter(), "zoom", console.getZoom()).start();
  }
  
  protected ReferenceHandler createReferenceHandler(AbstractReader reader, Attributes attributes) {
    return new ConsoleReferenceHandler(reader, attributes) {
      public void finished() {
        getElement().addReference(getReference());
      }
    };
  }

  protected ReferenceHandler createReferenceHandler(AbstractWriter writer, String tag, Reference reference) {
    return new ConsoleReferenceHandler(writer, tag, reference);
  }
}
