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

import java.io.IOException;

import org.xml.sax.*;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.Reference;
import jorgan.xml.*;
import jorgan.xml.handler.IntegerHandler;

/**
 * A handler for references to another object.
 */
public class ConsoleReferenceHandler extends ReferenceHandler {

  private int x;
  private int y;
  
  public ConsoleReferenceHandler(AbstractReader reader, Attributes attributes) {
    super(reader, attributes);
  }

  public ConsoleReferenceHandler(AbstractWriter writer, String tag, Reference reference) {
    super(writer, tag, reference);
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("x".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          x = getInteger();
        }
      };
    } else if ("y".equals(qName)) {
        new IntegerHandler(getReader()) {
          public void finished() {
            y = getInteger();
          }
        };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  public void children() throws IOException {
    super.children();

    Console.LocationReference reference = (Console.LocationReference)getReference(); 

    new IntegerHandler(getWriter(), "x", reference.getX()).start();
    new IntegerHandler(getWriter(), "y", reference.getY()).start();
  }

  protected Reference createReference(Element element) {
    Console.LocationReference reference = new Console.LocationReference(element); 
    
    reference.setX(x);
    reference.setY(y);
     
    return reference;  
  }
}
