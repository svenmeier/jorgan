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

public abstract class RegistratableHandler extends ActiveHandler {

  public RegistratableHandler(AbstractReader reader, Attributes attributes) {
    super(reader, attributes);
  }

  public RegistratableHandler(AbstractWriter writer, String tag) {
    super(writer, tag);
  }

  protected abstract Registratable getRegistratable();
  
  protected Active getActive() {
    return getRegistratable();
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("on".equals(qName)) {
      new BooleanHandler(getReader()) {
        public void finished() {
          getRegistratable().setOn(getBoolean());
        }
      };
    } else if ("inverse".equals(qName)) {
        new BooleanHandler(getReader()) {
          public void finished() {
            getRegistratable().setInverse(getBoolean());
          }
        };
    } else if ("onMessage".equals(qName)) {
      new MessageHandler(getReader()) {
        public void finished() {
          getRegistratable().setOnMessage(getMessage());
        }
      };
    } else if ("offMessage".equals(qName)) {
      new MessageHandler(getReader()) {
        public void finished() {
          getRegistratable().setOffMessage(getMessage());
        }
      };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  public void children() throws IOException {
    super.children();

    if (getRegistratable().isOn()) {
      new BooleanHandler(getWriter(), "on").start();
    }
    if (getRegistratable().isInverse()) {
      new BooleanHandler(getWriter(), "inverse").start();
    }
    if (getRegistratable().getOnMessage() != null) {
      new MessageHandler(getWriter(), "onMessage", getRegistratable().getOnMessage()).start();
    }
    if (getRegistratable().getOffMessage() != null) {
      new MessageHandler(getWriter(), "offMessage", getRegistratable().getOffMessage()).start();
    }
  }
}
