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

public abstract class ActivateableHandler extends ActiveHandler {

  public ActivateableHandler(AbstractReader reader, Attributes attributes) {
    super(reader, attributes);
  }

  public ActivateableHandler(AbstractWriter writer, String tag) {
    super(writer, tag);
  }

  protected abstract Activateable getActivateable();
  
  protected Responsive getActive() {
    return getActivateable();
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("active".equals(qName)) {
      new BooleanHandler(getReader()) {
        public void finished() {
          getActivateable().setActive(getBoolean());
        }
      };
    } else if ("locking".equals(qName)) {
      new BooleanHandler(getReader()) {
        public void finished() {
          getActivateable().setLocking(getBoolean());
        }
      };
    } else if ("activateMessage".equals(qName)) {
      new MessageHandler(getReader()) {
        public void finished() {
          getActivateable().setActivateMessage(getMessage());
        }
      };
    } else if ("deactivateMessage".equals(qName)) {
      new MessageHandler(getReader()) {
        public void finished() {
          getActivateable().setDeactivateMessage(getMessage());
        }
      };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  public void children() throws IOException {
    super.children();

    new BooleanHandler(getWriter(), "active", getActivateable().isActive()).start();
    new BooleanHandler(getWriter(), "locking", getActivateable().isLocking()).start();
    if (getActivateable().getActivateMessage() != null) {
      new MessageHandler(getWriter(), "activateMessage", getActivateable().getActivateMessage()).start();
    }
    if (getActivateable().getDeactivateMessage() != null) {
      new MessageHandler(getWriter(), "deactivateMessage", getActivateable().getDeactivateMessage()).start();
    }
  }
}
