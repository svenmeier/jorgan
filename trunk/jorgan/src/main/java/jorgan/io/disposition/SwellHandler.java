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

import jorgan.disposition.Continuous;
import jorgan.disposition.Swell;
import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;
import jorgan.xml.handler.IntegerHandler;

import org.xml.sax.Attributes;

public class SwellHandler extends ContinuousHandler {

    private Swell swell;

    public SwellHandler(AbstractReader reader, Attributes attributes) {
        super(reader, attributes);

        swell = new Swell();
    }

    public SwellHandler(AbstractWriter writer, String tag, Swell swell) {
        super(writer, tag);

        this.swell = swell;
    }

    public Swell getSwell() {
        return swell;
    }

    protected Continuous getContinuous() {
        return getSwell();
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {

        if ("volume".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    swell.setVolume(getInteger());
                }
            };
        } else if ("cutoff".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    swell.setCutoff(getInteger());
                }
            };
        } else {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    public void children() throws IOException {
        super.children();

        new IntegerHandler(getWriter(), "volume", swell.getVolume()).start();
        new IntegerHandler(getWriter(), "cutoff", swell.getCutoff()).start();
    }
}
