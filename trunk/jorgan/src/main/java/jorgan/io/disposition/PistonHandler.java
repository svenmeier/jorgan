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

public class PistonHandler extends ActiveHandler {

  private Piston piston;

  public PistonHandler(AbstractReader reader, Attributes attributes) {
    super(reader, attributes);

    piston = new Piston();
  }

  public PistonHandler(AbstractWriter writer, String tag, Piston piston) {
    super(writer, tag);

    this.piston = piston;
  }

  public Piston getPiston() {
    return piston;
  }

  public Active getActive() {
    return getPiston();
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("fixed".equals(qName)) {
      new BooleanHandler(getReader()) {
        public void finished() {
          piston.setFixed(getBoolean());
        }
      };
    } else if ("setWithGet".equals(qName)) {
      new BooleanHandler(getReader()) {
        public void finished() {
          piston.setSetWithGet(getBoolean());
        }
      };
    } else if ("getMessage".equals(qName)) {
      new MessageHandler(getReader()) {
        public void finished() {
          piston.setGetMessage(getMessage());
        }
      };
    } else if ("setMessage".equals(qName)) {
      new MessageHandler(getReader()) {
        public void finished() {
          piston.setSetMessage(getMessage());
        }
      };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  public void children() throws IOException {
    super.children();

    if (piston.isFixed()) {
      new BooleanHandler(getWriter(), "fixed").start();
    }
    if (piston.isSetWithGet()) {
      new BooleanHandler(getWriter(), "setWithGet").start();
    }
    if (piston.getGetMessage() != null) {
      new MessageHandler(getWriter(), "getMessage", piston.getGetMessage()).start();
    }
    if (piston.getSetMessage() != null) {
      new MessageHandler(getWriter(), "setMessage", piston.getSetMessage()).start();
    }
  }
  
  protected ReferenceHandler createReferenceHandler(AbstractReader reader, Attributes attributes) {
    return new PistonReferenceHandler(reader, attributes) {
      public void finished() {
        getActive().addReference(getReference());
      }
    };
  }

  protected ReferenceHandler createReferenceHandler(AbstractWriter writer, String tag, Reference reference) {
    return new PistonReferenceHandler(writer, tag, reference);
  }
}