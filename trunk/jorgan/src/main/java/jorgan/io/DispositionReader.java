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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import jorgan.disposition.Element;
import jorgan.io.disposition.*;
import jorgan.xml.*;

/**
 * A reader for dispositions.
 */
public class DispositionReader extends AbstractReader {

    /**
     * Mapping of ids to elements.
     */
    private Map elements = new HashMap();

    /**
     * All handlers for references.
     */
    private List referenceHandlers = new ArrayList();

    /**
     * Create a new reader for a disposition.
     * 
     * @param in
     *            the inputStream to read from
     */
    public DispositionReader(InputStream in) throws XMLFormatException {
        super(Conversion.convertAll(in));
    }

    protected ContentHandler createRootHandler() {
        return new RootHandler();
    }

    public Object read() throws IOException {
        Object object = super.read();

        for (int r = 0; r < referenceHandlers.size(); r++) {
            ReferenceHandler reference = (ReferenceHandler) referenceHandlers
                    .get(r);
            reference.resolve();
        }

        return object;
    }

    /**
     * Register an element.
     * 
     * @param element
     *            element to register
     */
    public void registerElement(String id, Element element) {
        elements.put(id, element);
    }

    /**
     * Look up an element.
     * 
     * @param id
     *            id of element to look up
     * @return element with given id
     */
    public Element lookupElement(String id) throws XMLFormatException {
        if (id == null) {
            throw new XMLFormatException("id must not be null");
        }
        Element element = (Element) elements.get(id);
        if (element == null) {
            throw new XMLFormatException("unknown id '" + id + "'");
        }
        return element;
    }

    /**
     * Add a handler for a reference.
     * 
     * @param handler
     *            handler to add
     */
    public void addReferenceHandler(ReferenceHandler handler) {
        referenceHandlers.add(handler);
    }

    /**
     * The root handler.
     */
    private class RootHandler extends DefaultHandler {

        public void startElement(String uri, String localName, String qName,
                Attributes attributes) throws SAXException {

            if ("organ".equals(qName)) {
                new OrganHandler(DispositionReader.this) {
                    public void finished() {
                        root = getOrgan();
                    }
                };
            } else {
                super.startElement(uri, localName, qName, attributes);
            }
        }
    }
}
