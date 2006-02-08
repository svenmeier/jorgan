/*
 * jOrgan - Java Virtual Pipe Organ
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
package jorgan.io.skin;

import java.awt.Font;
import java.io.IOException;

import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;
import jorgan.xml.handler.Handler;
import jorgan.xml.handler.IntegerHandler;
import jorgan.xml.handler.StringHandler;

import org.xml.sax.Attributes;

public class FontHandler extends Handler {

    private String name;

    private int style;

    private int size;

    private Font font;

    public FontHandler(AbstractWriter writer, String tag, Font font) {
        super(writer, tag);

        this.font = font;
    }

    public FontHandler(AbstractReader reader) {
        super(reader);
    }

    public Font getFont() {
        return font;
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {

        if ("name".equals(qName)) {
            new StringHandler(getReader()) {
                public void finished() {
                    name = getString();
                }
            };
        } else if ("style".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    style = getInteger();
                }
            };
        } else if ("size".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    size = getInteger();
                }
            };
        } else {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    protected void finish() {
        font = new Font(name, style, size);

        finished();
    }

    public void children() throws IOException {
        new StringHandler(getWriter(), "name", font.getName()).start();
        new IntegerHandler(getWriter(), "style", font.getStyle()).start();
        new IntegerHandler(getWriter(), "size", font.getSize()).start();
    }
}
