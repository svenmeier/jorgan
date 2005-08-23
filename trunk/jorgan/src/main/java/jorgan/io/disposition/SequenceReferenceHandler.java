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

import jorgan.disposition.Combination;
import jorgan.disposition.Element;
import jorgan.disposition.Reference;
import jorgan.disposition.Sequence;
import jorgan.xml.*;
import jorgan.xml.handler.BooleanHandler;

/**
 * A handler for references to another object.
 */
public class SequenceReferenceHandler extends ReferenceHandler {

  private boolean current;
  
  public SequenceReferenceHandler(AbstractReader reader, Attributes attributes) {
    super(reader, attributes);
  }

  public SequenceReferenceHandler(AbstractWriter writer, String tag, Reference reference) {
    super(writer, tag, reference);
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("current".equals(qName)) {
      new BooleanHandler(getReader()) {
        public void finished() {
          current = getBoolean();
        }
      };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  public void children() throws IOException {
    super.children();

    Sequence.SequenceReference reference = (Sequence.SequenceReference)getReference(); 
    if (reference.isCurrent()) {
      new BooleanHandler(getWriter(), "current").start();
    }
  }

  protected Reference createReference(Element element) {
    Sequence.SequenceReference reference = new Sequence.SequenceReference((Combination)element);
    
    reference.setCurrent(current);
     
    return reference;  
  }
}
