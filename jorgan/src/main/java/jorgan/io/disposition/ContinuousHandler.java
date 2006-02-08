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
import jorgan.disposition.Element;
import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;
import jorgan.xml.handler.BooleanHandler;
import jorgan.xml.handler.IntegerHandler;

import org.xml.sax.Attributes;

public abstract class ContinuousHandler extends ElementHandler {

    public ContinuousHandler(AbstractReader reader, Attributes attributes) {
        super(reader, attributes);
    }

    public ContinuousHandler(AbstractWriter writer, String tag) {
        super(writer, tag);
    }

    protected abstract Continuous getContinuous();

    protected Element getElement() {
        return getContinuous();
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {

        if ("position".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    getContinuous().setPosition(getInteger());
                }
            };
        } else if ("message".equals(qName)) {
            new MessageHandler(getReader()) {
                public void finished() {
                    getContinuous().setMessage(getMessage());
                }
            };
        } else if ("reverse".equals(qName)) {
            new BooleanHandler(getReader()) {
                public void finished() {
                    getContinuous().setReverse(getBoolean());
                }
            };
        } else if ("threshold".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    getContinuous().setThreshold(getInteger());
                }
            };
        } else if ("locking".equals(qName)) {
            new BooleanHandler(getReader()) {
                public void finished() {
                    getContinuous().setLocking(getBoolean());
                }
            };
        } else {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    public void children() throws IOException {
        super.children();

        new BooleanHandler(getWriter(), "locking", getContinuous().isLocking())
                .start();
        new IntegerHandler(getWriter(), "position", getContinuous()
                .getPosition()).start();
        new IntegerHandler(getWriter(), "threshold", getContinuous()
                .getThreshold()).start();
        new BooleanHandler(getWriter(), "reverse", getContinuous().isReverse())
                .start();
        if (getContinuous().getMessage() != null) {
            new MessageHandler(getWriter(), "message", getContinuous()
                    .getMessage()).start();
        }
    }
}
