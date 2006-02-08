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

import jorgan.disposition.Initiator;
import jorgan.disposition.Incrementer;
import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;
import jorgan.xml.handler.IntegerHandler;

import org.xml.sax.Attributes;

public class IncrementerHandler extends InitiatorHandler {

    private Incrementer incrementer;

    public IncrementerHandler(AbstractReader reader, Attributes attributes) {
        super(reader, attributes);

        incrementer = new Incrementer();
    }

    public IncrementerHandler(AbstractWriter writer, String tag, Incrementer incrementer) {
        super(writer, tag);

        this.incrementer = incrementer;
    }

    public Incrementer getIncrementer() {
        return incrementer;
    }

    public Initiator getInitiator() {
        return getIncrementer();
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {
        if ("delta".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    getIncrementer().setDelta(getInteger());
                }
            };
        } else {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    public void children() throws IOException {
        super.children();

        new IntegerHandler(getWriter(), "delta", getIncrementer().getDelta()).start();
    }
}