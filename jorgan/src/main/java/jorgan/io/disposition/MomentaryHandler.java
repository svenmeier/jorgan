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
import jorgan.disposition.Momentary;
import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;

import org.xml.sax.Attributes;

public abstract class MomentaryHandler extends ElementHandler {

    public MomentaryHandler(AbstractReader reader, Attributes attributes) {
        super(reader, attributes);
    }

    public MomentaryHandler(AbstractWriter writer, String tag) {
        super(writer, tag);
    }

    protected Element getElement() {
        return getMomentary();
    }

    protected abstract Momentary getMomentary();

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {
        if ("shortcut".equals(qName)) {
            new ShortcutHandler(getReader()) {
                public void finished() {
                    getMomentary().setShortcut(getShortcut());
                }
            };
        } else {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    public void children() throws IOException {
        super.children();

        if (getMomentary().getShortcut() != null) {
            new ShortcutHandler(getWriter(), "shortcut", getMomentary()
                    .getShortcut()).start();
        }
    }
}