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

public class ImageHandler extends Handler {

  private Image image;

  /**
   * Constructor.
   */
  public ImageHandler(AbstractReader reader) {
    super(reader);

    image = new Image();
  }
  
  public ImageHandler(AbstractWriter writer, String tag, Image image) {
    super(writer, tag);
    
    this.image = image;
  }

  public Image getImage() {
    return image;
  }

  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    if ("file".equals(qName)) {
      new StringHandler(getReader()) {
        public void finished() {
          image.setFile(getString());
        }
      };
    } else if ("location".equals(qName)) {
        new PointHandler(getReader()) {
          public void finished() {
            image.setLocation(getPoint());
          }
        };
    } else {
      super.startElement(uri, localName, qName, attributes);
    }
  }

  public void children() throws IOException {
    super.children();

    new StringHandler(getWriter(), "file"    , image.getFile()    ).start();
    new PointHandler (getWriter(), "location", image.getLocation()).start();
  }
}
