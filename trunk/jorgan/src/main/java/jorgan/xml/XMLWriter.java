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
import java.util.*;

/**
 * A writer for XML data.
 */
public class XMLWriter {

  /**
   * The encoding used to write XML data.
   */
  private static final String ENCODING = "UTF-8";

  /**
   * The writer to write XML to.
   */
  private BufferedWriter out;

  /**
   * Current indentation, increased on start of an element
   * and decreased on end of element.
   */
  private StringBuffer indent = new StringBuffer();

  /**
   * Is the start of an element not finished yet.
   */
  private boolean elementStarted = false;

  /**
   * Is the writer on a new line.
   */
  private boolean onNewLine = true;

  /**
   * Create a new XMLWriter.
   *
   * @param out   the outputstream to write XML data to
   * @throws IOException if output for XML data couldn't be set up
   */
  public XMLWriter(OutputStream out) throws IOException {

    this.out = new BufferedWriter(new OutputStreamWriter(out, ENCODING));
  }

  /**
   * Start the document.
   *
   * @throws IOException if XML data couldn't be written
   */
  public void startDocument() throws IOException {
    out.write("<?xml version=\"1.0\" encoding=\"" + ENCODING + "\" ?>");
    out.newLine();
  }

  /**
   * Close this writer.
   *
   * @throws IOException if outputstream used to write XML data couldn't be closed
   */
  public void close() throws IOException {
    out.close();
  }

  /**
   * Start an element.
   *
   * @param tag          the tag name of the element to start
   * @throws IOException if XML data couldn't be written
   */
  public void startElement(String tag) throws IOException {
    startElement(tag, null);
  }

  /**
   * Start an element.
   *
   * @param tag          the tag name of the element to start
   * @param attributes   attributes of element to start
   * @throws IOException if XML data couldn't be written
   */
  public void startElement(String tag, Map attributes) throws IOException {
    checkElementStarted(true);
    checkOnNewLine();

    out.write("<");
    out.write(tag);

    if (attributes != null) {
      attributes(attributes);
    }

    elementStarted = true;
    indent.append("  ");
  }

  /**
   * Write an attribute.
   *
   * @param key   key of attribute
   * @param value value of attribute
   * @throws IOException if XML data couldn't be written
   */
  public void attribute(String key, Object value) throws IOException {
    if (value != null) {
      out.write(" ");
      out.write(key);
      out.write("=\"");
      out.write(value.toString());
      out.write("\"");
    }
  }

  /**
   * Write attributes.
   *
   * @param attributes   attributes to write
   * @throws IOException if XML data couldn't be written
   */
  public void attributes(Map attributes) throws IOException {
    Iterator iterator = attributes.keySet().iterator();
    while (iterator.hasNext()) {
      String key   = (String)iterator.next();
      Object value = attributes.get(key);

      attribute(key, value);
    }
  }

  /**
   * Write characters.
   *
   * @param characters  characters to write
   * @throws IOException if XML data couldn't be written
   */
  public void characters(String characters) throws IOException {
    if (characters != null) {
      checkElementStarted(false);
      checkOnNewLine();

      StringBuffer buffer = new StringBuffer(characters.length() + 50);
      for (int c = 0; c < characters.length(); c++) {
        char character = characters.charAt(c);
        switch (character) {
          case '<':
            buffer.append("&lt;");
            break;
          case '>':
            buffer.append("&gt;");
            break;
          case '&':
            buffer.append("&amp;");
            break;
          default:
            buffer.append(character);
        }
      }
      out.write(buffer.toString());
    }
  }

  /**
   * End an element.
   *
   * @param tag   tag name of element
   * @throws IOException if XML data couldn't be written
   */
  public void endElement(String tag) throws IOException {
    indent.delete(indent.length() - 2, indent.length());

    if (elementStarted) {
      out.write(" />");
      out.newLine();
    } else {
      checkOnNewLine();
      out.write("</");
      out.write(tag);
      out.write(">");
      out.newLine();
    }

    onNewLine = true;
    elementStarted = false;
  }

  /**
   * Check if the start of an element wasn't finished before
   *
   * @param newLine   should a new line be printed
   * @throws IOException if XML data couldn't be written
   */
  private void checkElementStarted(boolean newLine) throws IOException {
    if (elementStarted) {
      out.write(">");
      if (newLine) {
        out.newLine();
        onNewLine = true;
      }
    }
    elementStarted = false;
  }

  /**
   * Check if writer is on a new line, writing the current indentation.
   *
   * @throws IOException if XML data couldn't be written
   */
  private void checkOnNewLine() throws IOException {
    if (onNewLine) {
      out.write(indent.toString());
      onNewLine = false;
    }
  }
}