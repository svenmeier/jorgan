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

public class SequenceHandler extends ActiveHandler {

  private Sequence sequence;

  public SequenceHandler(AbstractReader reader, Attributes attributes) {
    super(reader, attributes);

    sequence = new Sequence();
  }

  public SequenceHandler(AbstractWriter writer, String tag, Sequence sequence) {
    super(writer, tag);

    this.sequence = sequence;
  }

  public Sequence getSequence() {
    return sequence;
  }

  public Responsive getActive() {
    return getSequence();
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("nextMessage".equals(qName)) {
      new MessageHandler(getReader()) {
        public void finished() {
          sequence.setNextMessage(getMessage());
        }
      };
    } else if ("previousMessage".equals(qName)) {
      new MessageHandler(getReader()) {
        public void finished() {
            sequence.setPreviousMessage(getMessage());
        }
      };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  public void children() throws IOException {
    super.children();

    if (sequence.getNextMessage() != null) {
      new MessageHandler(getWriter(), "nextMessage", sequence.getNextMessage()).start();
    }
    if (sequence.getPreviousMessage() != null) {
      new MessageHandler(getWriter(), "previousMessage", sequence.getPreviousMessage()).start();
    }
  }
  
  protected ReferenceHandler createReferenceHandler(AbstractReader reader, Attributes attributes) {
    return new SequenceReferenceHandler(reader, attributes) {
      public void finished() {
        getSequence().addReference(getReference());
      }
    };
  }

  protected ReferenceHandler createReferenceHandler(AbstractWriter writer, String tag, Reference reference) {
    return new SequenceReferenceHandler(writer, tag, reference);
  }
}