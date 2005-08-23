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

public class CombinationHandler extends ActiveHandler {

  private Combination combination;

  public CombinationHandler(AbstractReader reader, Attributes attributes) {
    super(reader, attributes);

    combination = new Combination();
  }

  public CombinationHandler(AbstractWriter writer, String tag, Combination combination) {
    super(writer, tag);

    this.combination = combination;
  }

  public Combination getCombination() {
    return combination;
  }

  public Responsive getActive() {
    return getCombination();
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("fixed".equals(qName)) {
      new BooleanHandler(getReader()) {
        public void finished() {
          combination.setFixed(getBoolean());
        }
      };
    } else if ("captureWithRecall".equals(qName)) {
      new BooleanHandler(getReader()) {
        public void finished() {
          combination.setCaptureWithRecall(getBoolean());
        }
      };
    } else if ("recallMessage".equals(qName)) {
      new MessageHandler(getReader()) {
        public void finished() {
          combination.setRecallMessage(getMessage());
        }
      };
    } else if ("captureMessage".equals(qName)) {
      new MessageHandler(getReader()) {
        public void finished() {
          combination.setCaptureMessage(getMessage());
        }
      };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  public void children() throws IOException {
    super.children();

    if (combination.isFixed()) {
      new BooleanHandler(getWriter(), "fixed").start();
    }
    if (combination.isCaptureWithRecall()) {
      new BooleanHandler(getWriter(), "captureWithRecall").start();
    }
    if (combination.getRecallMessage() != null) {
      new MessageHandler(getWriter(), "recallMessage", combination.getRecallMessage()).start();
    }
    if (combination.getCaptureMessage() != null) {
      new MessageHandler(getWriter(), "captureMessage", combination.getCaptureMessage()).start();
    }
  }
  
  protected ReferenceHandler createReferenceHandler(AbstractReader reader, Attributes attributes) {
    return new CombinationReferenceHandler(reader, attributes) {
      public void finished() {
        getCombination().addReference(getReference());
      }
    };
  }

  protected ReferenceHandler createReferenceHandler(AbstractWriter writer, String tag, Reference reference) {
    return new CombinationReferenceHandler(writer, tag, reference);
  }
}