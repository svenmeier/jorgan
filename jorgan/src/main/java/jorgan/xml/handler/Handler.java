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
package jorgan.xml.handler;

import java.io.IOException;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import jorgan.xml.*;

/**
 * A handler writes an object to XML.
 */
public class Handler extends DefaultHandler {

  /**
   * The parental handler.
   */
  private ContentHandler parent;
  
  /**
   * The reader.
   */
  private AbstractReader reader;

  /**
   * The reader.
   */
  private AbstractWriter writer;

  /**
   * The character content (if any).
   */
  private StringBuffer characters = new StringBuffer();

  private String tag;

  /**
   * Create a new handler with the given tag.
   * 
   * @param tag   the tag to use for this handler 
   */
  public Handler(AbstractWriter writer, String tag){
    this.writer = writer;    
    this.tag    = tag;
  }

  /**
   * Constructor.
   *
   * @param parent      the parental handler
   */
  public Handler(AbstractReader reader) {
    this.reader = reader;

    this.parent = reader.getXMLReader().getContentHandler();

    reader.getXMLReader().setContentHandler(this);
  }

  public AbstractReader getReader() {
    return reader;
  }
  
  public AbstractWriter getWriter() {
    return writer;
  }
  
  public void start() throws IOException {
    startElement(writer.getXMLWriter());
    attributes  (writer.getXMLWriter());
    children    ();
    characters  (writer.getXMLWriter());
    endElement  (writer.getXMLWriter());
  }

  public void startElement(XMLWriter writer) throws IOException {
    writer.startElement(tag);
  }

  public void attributes(XMLWriter writer) throws IOException {
  }

  public void characters(XMLWriter writer) throws IOException {
  }

  public void children() throws IOException {
  }

  public void endElement(XMLWriter writer) throws IOException {
    writer.endElement(tag);
  }
  
  /**
   * @see org.xml.sax.ContentHandler
   */
  public void startElement(String uri, String localName,
                           String qName, Attributes attributes) {

    new Handler(reader);
  }

  /**
   * @see org.xml.sax.ContentHandler
   */
  public void characters(char[] ch, int start, int length) {
    characters.append(ch, start, length);
  }

  /**
   * @see org.xml.sax.ContentHandler
   */
  public void endElement(String uri, String localName, String qName) {
    finish();

    if (parent != null) {
      reader.getXMLReader().setContentHandler(parent);
    }
  }

  /**
   * Get the character content as a string.
   *
   * @return  string content
   */
  protected StringBuffer getCharacters() {
    return characters;
  }

  /**
   * Callback that request this handler to finish.
   * <br>
   * Subclasses should override this method to create
   * the handled object and not forget to call
   * {@link #finished()} afterwards.
   */
  protected void finish() {
    finished();
  }

  /**
   * Callback that notifies about that this handler
   * is finished.
   * <br>
   * Subclasses should override this method to get
   * hold of the handled object.
   */
  public void finished() {
  }
}