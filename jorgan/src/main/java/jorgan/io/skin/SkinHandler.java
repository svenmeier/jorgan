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
package jorgan.io.skin;

import java.io.*;

import org.xml.sax.*;

import jorgan.skin.*;
import jorgan.xml.*;
import jorgan.xml.handler.*;

public class SkinHandler extends Handler {

  private Skin skin;

  /**
   * Constructor.
   */
  public SkinHandler(AbstractReader reader) {
    super(reader);

    skin = new Skin();
  }
  
  public SkinHandler(AbstractWriter writer, String tag, Skin skin) {
    super(writer, tag);
    
    this.skin = skin;
  }

  public Skin getSkin() {
    return skin;
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("style".equals(qName)) {
      new StyleHandler(getReader()) {
        public void finished() {
          skin.addStyle(getStyle());
        }
      };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  public void children() throws IOException {
    super.children();

    for (int s = 0; s < skin.getStyleCount(); s++) {
      new StyleHandler(getWriter(), "style", skin.getStyle(s)).start();
    }
  }
}
