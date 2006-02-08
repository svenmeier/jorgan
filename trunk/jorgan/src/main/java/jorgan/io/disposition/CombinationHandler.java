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

import jorgan.disposition.Activateable;
import jorgan.disposition.Combination;
import jorgan.disposition.Element;
import jorgan.disposition.Initiator;
import jorgan.disposition.Reference;
import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;
import jorgan.xml.XMLWriter;

import org.xml.sax.Attributes;

public class CombinationHandler extends InitiatorHandler {

    private Combination combination;

    public CombinationHandler(AbstractReader reader, Attributes attributes) {
        super(reader, attributes);

        combination = new Combination();
    }

    public CombinationHandler(AbstractWriter writer, String tag,
            Combination combination) {
        super(writer, tag);

        this.combination = combination;
    }

    public Combination getCombination() {
        return combination;
    }

    public Initiator getInitiator() {
        return getCombination();
    }

    protected ReferenceHandler createReferenceHandler(AbstractReader reader,
            Attributes attributes) {
        return new CombinationReferenceHandler(reader, attributes) {
            public void finished() {
                getCombination().addReference(getReference());
            }
        };
    }

    protected ReferenceHandler createReferenceHandler(AbstractWriter writer,
            String tag, Reference reference) {
        return new CombinationReferenceHandler(writer, tag, reference);
    }

    private static class CombinationReferenceHandler extends ReferenceHandler {

        public CombinationReferenceHandler(AbstractReader reader,
                Attributes attributes) {
            super(reader, attributes);
        }

        public CombinationReferenceHandler(AbstractWriter writer, String tag,
                Reference reference) {
            super(writer, tag, reference);
        }

        public void characters(XMLWriter writer) throws IOException {
            Combination.CombinationReference reference = (Combination.CombinationReference) getReference();

            StringBuffer text = new StringBuffer(128);
            for (int l = 0; l < 128; l++) {
                text.append(reference.isActive(l) ? '1' : '0');
            }
            writer.characters(text.toString());
        }

        protected Reference createReference(Element element) {
            Combination.CombinationReference reference = new Combination.CombinationReference(
                    (Activateable) element);

            String text = getCharacters();
            for (int l = 0; l < 128; l++) {
                reference.setActive(l, text.charAt(l) == '1' ? true : false);
            }

            return reference;
        }
    }
}