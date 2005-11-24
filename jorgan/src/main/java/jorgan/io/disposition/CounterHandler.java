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
import jorgan.xml.handler.IntegerHandler;

public abstract class CounterHandler extends ActiveHandler {

  public CounterHandler(AbstractReader reader, Attributes attributes) {
    super(reader, attributes);
  }

  public CounterHandler(AbstractWriter writer, String tag) {
    super(writer, tag);
  }

  protected abstract Counter getCounter();
  
  public Responsive getActive() {
    return getCounter();
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("current".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          getCounter().setCurrent(getInteger());
        }
      };
    } else if ("nextMessage".equals(qName)) {
      new MessageHandler(getReader()) {
        public void finished() {
          getCounter().setNextMessage(getMessage());
        }
      };
    } else if ("previousMessage".equals(qName)) {
      new MessageHandler(getReader()) {
        public void finished() {
          getCounter().setPreviousMessage(getMessage());
        }
      };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  public void children() throws IOException {
    super.children();

    new IntegerHandler(getWriter(), "current", getCounter().getCurrent()).start();
    
    if (getCounter().getNextMessage() != null) {
      new MessageHandler(getWriter(), "nextMessage", getCounter().getNextMessage()).start();
    }
    if (getCounter().getPreviousMessage() != null) {
      new MessageHandler(getWriter(), "previousMessage", getCounter().getPreviousMessage()).start();
    }
  }  
}