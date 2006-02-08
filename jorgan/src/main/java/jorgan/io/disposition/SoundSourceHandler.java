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
import jorgan.disposition.SoundSource;
import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;
import jorgan.xml.handler.IntegerHandler;
import jorgan.xml.handler.StringHandler;

import org.xml.sax.Attributes;

public class SoundSourceHandler extends ElementHandler {

    private SoundSource soundSource;

    /**
     * Constructor.
     */
    public SoundSourceHandler(AbstractReader reader, Attributes attributes) {
        super(reader, attributes);

        soundSource = new SoundSource();
    }

    public SoundSourceHandler(AbstractWriter writer, String tag,
            SoundSource soundSource) {
        super(writer, tag);

        this.soundSource = soundSource;
    }

    public SoundSource getSoundSource() {
        return soundSource;
    }

    protected Element getElement() {
        return getSoundSource();
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {

        if ("delay".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    soundSource.setDelay(getInteger());
                }
            };
        } else if ("device".equals(qName)) {
            new StringHandler(getReader()) {
                public void finished() {
                    soundSource.setDevice(getString());
                }
            };
        } else if ("type".equals(qName)) {
            new StringHandler(getReader()) {
                public void finished() {
                    soundSource.setType(getString());
                }
            };
        } else if ("bank".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    soundSource.setBank(getInteger());
                }
            };
        } else if ("samples".equals(qName)) {
            new StringHandler(getReader()) {
                public void finished() {
                    soundSource.setSamples(getString());
                }
            };
        } else {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    public void children() throws IOException {
        super.children();

        if (soundSource.getDevice() != null) {
            new StringHandler(getWriter(), "device", soundSource.getDevice())
                    .start();
        }
        if (soundSource.getType() != null) {
            new StringHandler(getWriter(), "type", soundSource.getType())
                    .start();
        }
        if (soundSource.getSamples() != null) {
            new StringHandler(getWriter(), "samples", soundSource.getSamples())
                    .start();
        }
        new IntegerHandler(getWriter(), "delay", soundSource.getDelay())
                .start();
        new IntegerHandler(getWriter(), "bank", soundSource.getBank()).start();
    }
}
