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

import java.io.IOException;

import jorgan.disposition.Element;
import jorgan.disposition.Reference;
import jorgan.io.DispositionReader;
import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;
import jorgan.xml.XMLWriter;
import jorgan.xml.handler.Handler;
import jorgan.xml.handler.StringHandler;

import org.xml.sax.Attributes;

public abstract class ElementHandler extends Handler {

    public static final String ID_ATTRIBUTE_NAME = "id";

    private String id;

    public ElementHandler(AbstractReader reader, Attributes attributes) {
        super(reader);

        id = attributes.getValue(ID_ATTRIBUTE_NAME);
    }

    public ElementHandler(AbstractWriter writer, String tag) {
        super(writer, tag);
    }

    protected abstract Element getElement();

    protected void finish() {
        ((DispositionReader) getReader()).registerElement(id, getElement());

        super.finish();
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {
        if ("name".equals(qName)) {
            new StringHandler(getReader()) {
                public void finished() {
                    getElement().setName(getString());
                }
            };
        } else if ("description".equals(qName)) {
            new StringHandler(getReader()) {
                public void finished() {
                    getElement().setDescription(getString());
                }
            };
        } else if ("style".equals(qName)) {
            new StringHandler(getReader()) {
                public void finished() {
                    getElement().setStyle(getString());
                }
            };
        } else if ("reference".equals(qName)) {
            createReferenceHandler(getReader(), attributes);
        } else {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    public void attributes(XMLWriter writer) throws IOException {
        if (getElement().hasReferrer()) {
            id = "" + System.identityHashCode(getElement());

            writer.attribute(ID_ATTRIBUTE_NAME, id);
        }
        super.attributes(writer);
    }

    public void children() throws IOException {
        super.children();

        new StringHandler(getWriter(), "name", getElement().getName()).start();
        if (!"".equals(getElement().getDescription())) {
            new StringHandler(getWriter(), "description", getElement()
                    .getDescription()).start();
        }
        if (getElement().getStyle() != null) {
            new StringHandler(getWriter(), "style", getElement().getStyle())
                    .start();
        }

        for (int r = 0; r < getElement().getReferenceCount(); r++) {
            Reference reference = getElement().getReference(r);
            createReferenceHandler(getWriter(), "reference", reference).start();
        }
    }

    protected ReferenceHandler createReferenceHandler(AbstractReader reader,
            Attributes attributes) {
        return new ReferenceHandler(reader, attributes) {
            public void finished() {
                getElement().addReference(getReference());
            }
        };
    }

    protected ReferenceHandler createReferenceHandler(AbstractWriter writer,
            String tag, Reference reference) {
        return new ReferenceHandler(writer, tag, reference);
    }
}