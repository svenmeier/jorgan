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

public abstract class SliderHandler extends ActiveHandler {

  public SliderHandler(AbstractReader reader, Attributes attributes) {
    super(reader, attributes);
  }

  public SliderHandler(AbstractWriter writer, String tag) {
    super(writer, tag);
  }

  protected abstract Slider getSlider();

  protected Responsive getActive() {
    return getSlider();
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("position".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          getSlider().setPosition(getInteger());
        }
      };
    } else if ("message".equals(qName)) {
      new MessageHandler(getReader()) {
        public void finished() {
          getSlider().setMessage(getMessage());
        }
      };
    } else if ("threshold".equals(qName)) {
      new IntegerHandler(getReader()) {
        public void finished() {
          getSlider().setThreshold(getInteger());
        }
      };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  public void children() throws IOException {
    super.children();

    new IntegerHandler(getWriter(), "position", getSlider().getPosition()).start();
    new IntegerHandler(getWriter(), "threshold", getSlider().getThreshold()).start();
    if (getSlider().getMessage() != null) {
      new MessageHandler(getWriter(), "message" , getSlider().getMessage()).start();
    }
  }
}
