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
package jorgan.io;

import java.io.*;

import jorgan.disposition.*;
import jorgan.xml.*;
import jorgan.xml.handler.*;
import jorgan.io.disposition.*;

/**
 * A writer for dispositions.
 */
public class DispositionWriter extends AbstractWriter {

  /**
   * Constructor.
   *
   * @param out         outputstream to write disposition to
   */
  public DispositionWriter(OutputStream out) {
    super(out);
  }

  /**
   * Create the handler for the root element.
   *
   * @param root  the root to get a handler for
   * @return      the handler for the given root
   */
  protected Handler createRootHandler(Object root) {

    return new OrganHandler(DispositionWriter.this, "organ", (Organ)root);
  }
}