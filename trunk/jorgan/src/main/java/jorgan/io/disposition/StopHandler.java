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

public class StopHandler extends KeyableHandler {

  private Stop stop;

  public StopHandler(AbstractReader reader, Attributes attributes) {
    super(reader, attributes);

    stop = new Stop();
  }

  public StopHandler(AbstractWriter writer, String tag, Stop stop) {
    super(writer, tag);

    this.stop = stop;
  }

  public Stop getStop() {
    return stop;
  }

  protected Keyable getKeyable() {
    return stop;
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("program".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          stop.setProgram(getInteger());
        }
      };
    } else if ("allocation".equals(qName)) {
        new IntegerHandler(getReader()) {
          public void finished() {
            stop.setAllocation(getInteger());
          }
        };
    } else if ("velocity".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          stop.setVelocity(getInteger());
        }
      };
    } else if ("volume".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          stop.setVolume(getInteger());
        }
      };
    } else if ("pan".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          stop.setPan(getInteger());
        }
      };
    } else if ("bend".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          stop.setBend(getInteger());
        }
      };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  public void children() throws IOException {
    super.children();

    new IntegerHandler(getWriter(), "program"   , stop.getProgram   ()).start();
    new IntegerHandler(getWriter(), "allocation", stop.getAllocation()).start();
    new IntegerHandler(getWriter(), "velocity"  , stop.getVelocity  ()).start();
    new IntegerHandler(getWriter(), "volume"    , stop.getVolume    ()).start();
    new IntegerHandler(getWriter(), "pan"       , stop.getPan       ()).start();
    new IntegerHandler(getWriter(), "bend"      , stop.getBend      ()).start();
  }
}
