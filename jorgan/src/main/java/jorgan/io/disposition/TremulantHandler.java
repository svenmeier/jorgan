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

public class TremulantHandler extends RegistratableHandler {

  private Tremulant tremulant;

  public TremulantHandler(AbstractReader reader, Attributes attributes) {
    super(reader, attributes);

    tremulant = new Tremulant();
  }

  public TremulantHandler(AbstractWriter writer, String tag, Tremulant tremulant) {
    super(writer, tag);

    this.tremulant = tremulant;
  }

  public Tremulant getTremulant() {
    return tremulant;
  }

  protected Registratable getRegistratable() {
    return getTremulant();
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("frequency".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          tremulant.setFrequency(getInteger());
        }
      };
    } else if ("amplitude".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          tremulant.setAmplitude(getInteger());
        }
      };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  public void children() throws IOException {
    super.children();

    new IntegerHandler(getWriter(), "frequency", tremulant.getFrequency()).start();
    new IntegerHandler(getWriter(), "amplitude", tremulant.getAmplitude()).start();
  }
}