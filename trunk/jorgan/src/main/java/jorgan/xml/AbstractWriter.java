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
package jorgan.xml;

import java.io.*;

import jorgan.xml.handler.*;

/**
 * An abstract base class for writers that produce xml output.
 */
public abstract class AbstractWriter {

  private OutputStream out;
  
  private XMLWriter writer;

  /**
   * Constructor.
   *
   * @param out         outputstream to write to
   */
  public AbstractWriter(OutputStream out) {

    this.out = out;
  }

  public XMLWriter getXMLWriter() {
    return writer; 
  }

  /**
   * Write an object.
   *
   * @param object   object to write
   */
  public void write(Object root) throws IOException {

    writer = new XMLWriter(out);

    writer.startDocument();

    createRootHandler(root).start();

    writer.close();
  }
  
  /**
   * Create the handler for the root element.
   *
   * @param root  the root to get a handler for
   * @return      the handler for the given root
   */
  protected abstract Handler createRootHandler(Object root);
}