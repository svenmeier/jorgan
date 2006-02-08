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

import org.xml.sax.Attributes;

/**
 * A handler for references to another object.
 */
public class ReferenceHandler extends Handler {

    private Reference reference;

    private String ref;

    public ReferenceHandler(AbstractReader reader, Attributes attributes) {
        super(reader);

        ref = attributes.getValue(ElementHandler.ID_ATTRIBUTE_NAME);

        ((DispositionReader) reader).addReferenceHandler(this);
    }

    public ReferenceHandler(AbstractWriter writer, String tag,
            Reference reference) {
        super(writer, tag);

        ref = "" + System.identityHashCode(reference.getElement());

        this.reference = reference;
    }

    public void attributes(XMLWriter writer) throws IOException {

        writer.attribute(ElementHandler.ID_ATTRIBUTE_NAME, ref);
    }

    public Reference getReference() {
        return reference;
    }

    /**
     * Overriden to delay call of <code>finished()</code> to resolve.
     */
    protected void finish() {
    }

    public void resolve() throws IOException {
        reference = createReference(((DispositionReader) getReader())
                .lookupElement(ref));

        finished();
    }

    protected Reference createReference(Element element) {
        return new Reference(element);
    }
}
