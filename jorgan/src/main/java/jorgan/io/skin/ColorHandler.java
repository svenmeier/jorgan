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
package jorgan.io.skin;

import java.awt.Color;
import java.io.IOException;

import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;
import jorgan.xml.handler.Handler;
import jorgan.xml.handler.IntegerHandler;

import org.xml.sax.Attributes;

public class ColorHandler extends Handler {

    private int red = 0;

    private int green = 0;

    private int blue = 0;

    private int alpha = 255;

    private Color color;

    public ColorHandler(AbstractWriter writer, String tag, Color color) {
        super(writer, tag);

        this.color = color;
    }

    public ColorHandler(AbstractReader reader) {
        super(reader);
    }

    public Color getColor() {
        return color;
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {

        if ("red".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    red = getInteger();
                }
            };
        } else if ("green".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    green = getInteger();
                }
            };
        } else if ("blue".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    blue = getInteger();
                }
            };
        } else if ("alpha".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    alpha = getInteger();
                }
            };
        } else {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    protected void finish() {
        color = new Color(red, green, blue, alpha);

        finished();
    }

    public void children() throws IOException {
        new IntegerHandler(getWriter(), "red", color.getRed()).start();
        new IntegerHandler(getWriter(), "green", color.getGreen()).start();
        new IntegerHandler(getWriter(), "blue", color.getBlue()).start();
        new IntegerHandler(getWriter(), "alpha", color.getAlpha()).start();
    }
}
