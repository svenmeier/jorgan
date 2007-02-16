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

import javax.xml.parsers.*;
import org.xml.sax.*;

/**
 * A reader for xml.
 */
public abstract class AbstractReader {

    /**
     * The inputStream to read from.
     */
    private InputStream in;

    /**
     * The XML parser.
     */
    private XMLReader reader;

    /**
     * The root object read.
     */
    protected Object root;

    /**
     * Create a new reader.
     * 
     * @param in
     *            the inputStream to read from
     */
    public AbstractReader(InputStream in) {

        this.in = new BufferedInputStream(in);
    }

    /**
     * Read the object.
     * 
     * @return the read object
     * @throws IOException
     *             if an IO operation failes
     */
    public Object read() throws IOException {

        try {
            reader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();

            reader.setContentHandler(createRootHandler());
            reader.parse(new InputSource(in));

            in.close();

            return root;
        } catch (ParserConfigurationException ex) {
            throw new Error(ex);
        } catch (SAXException ex) {
            throw new XMLFormatException(ex);
        } catch (RuntimeException ex) {
            throw new XMLFormatException(ex);
        }
    }

    /**
     * Get the reader for xml data.
     * 
     * @return the xml reader
     */
    public XMLReader getXMLReader() {
        return reader;
    }

    /**
     * Create the handler for the root.
     * 
     * @return the root handler
     */
    protected abstract ContentHandler createRootHandler();
}
