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

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.Reference;
import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;
import jorgan.xml.handler.DoubleHandler;
import jorgan.xml.handler.FloatHandler;
import jorgan.xml.handler.IntegerHandler;
import jorgan.xml.handler.StringHandler;

import org.xml.sax.Attributes;

public class ConsoleHandler extends ElementHandler {

    private Console console;

    /**
     * Constructor.
     */
    public ConsoleHandler(AbstractReader reader, Attributes attributes) {
        super(reader, attributes);

        console = new Console();
    }

    public ConsoleHandler(AbstractWriter writer, String tag, Console console) {
        super(writer, tag);

        this.console = console;
    }

    public Console getConsole() {
        return console;
    }

    protected Element getElement() {
        return console;
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {

        if ("device".equals(qName)) {
            new StringHandler(getReader()) {
                public void finished() {
                    console.setDevice(getString());
                }
            };
        } else if ("skin".equals(qName)) {
            new StringHandler(getReader()) {
                public void finished() {
                    console.setSkin(getString());
                }
            };
        } else if ("zoom".equals(qName)) {
            new FloatHandler(getReader()) {
                public void finished() {
                    console.setZoom(getFloat());
                }
            };
        } else if ("screen".equals(qName)) {
            new StringHandler(getReader()) {
                public void finished() {
                    console.setScreen(getString());
                }
            };
        } else {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    public void children() throws IOException {
        super.children();

        if (console.getDevice() != null) {
            new StringHandler(getWriter(), "device", console.getDevice())
                    .start();
        }
        if (console.getSkin() != null) {
            new StringHandler(getWriter(), "skin", console.getSkin()).start();
        }
        if (console.getScreen() != null) {
            new StringHandler(getWriter(), "screen", console.getScreen())
                    .start();
        }
        new DoubleHandler(getWriter(), "zoom", console.getZoom()).start();
    }

    protected ReferenceHandler createReferenceHandler(AbstractReader reader,
            Attributes attributes) {
        return new ConsoleReferenceHandler(reader, attributes) {
            public void finished() {
                getElement().addReference(getReference());
            }
        };
    }

    protected ReferenceHandler createReferenceHandler(AbstractWriter writer,
            String tag, Reference reference) {
        return new ConsoleReferenceHandler(writer, tag, reference);
    }

    private static class ConsoleReferenceHandler extends ReferenceHandler {

        private int x;

        private int y;

        public ConsoleReferenceHandler(AbstractReader reader,
                Attributes attributes) {
            super(reader, attributes);
        }

        public ConsoleReferenceHandler(AbstractWriter writer, String tag,
                Reference reference) {
            super(writer, tag, reference);
        }

        public void startElement(String uri, String localName, String qName,
                Attributes attributes) {

            if ("x".equals(qName)) {
                new IntegerHandler(getReader()) {
                    public void finished() {
                        x = getInteger();
                    }
                };
            } else if ("y".equals(qName)) {
                new IntegerHandler(getReader()) {
                    public void finished() {
                        y = getInteger();
                    }
                };
            } else {
                super.startElement(uri, localName, qName, attributes);
            }
        }

        public void children() throws IOException {
            super.children();

            Console.LocationReference reference = (Console.LocationReference) getReference();

            new IntegerHandler(getWriter(), "x", reference.getX()).start();
            new IntegerHandler(getWriter(), "y", reference.getY()).start();
        }

        protected Reference createReference(Element element) {
            Console.LocationReference reference = new Console.LocationReference(
                    element);

            reference.setX(x);
            reference.setY(y);

            return reference;
        }
    }
}