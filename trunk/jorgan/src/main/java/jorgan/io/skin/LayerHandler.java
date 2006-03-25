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

import java.io.IOException;

import jorgan.skin.Layer;
import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;
import jorgan.xml.handler.Handler;
import jorgan.xml.handler.IntegerHandler;

import org.xml.sax.Attributes;

public abstract class LayerHandler extends Handler {

    /**
     * Constructor.
     */
    public LayerHandler(AbstractReader reader) {
        super(reader);
    }

    public LayerHandler(AbstractWriter writer, String tag) {
        super(writer, tag);
    }

    public abstract Layer getLayer();

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {

        if ("width".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    getLayer().setWidth(getInteger());
                }
            };
        } else if ("height".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    getLayer().setHeight(getInteger());
                }
            };
        } else if ("padding".equals(qName)) {
            new InsetsHandler(getReader()) {
                public void finished() {
                    getLayer().setPadding(getInsets());
                }
            };
        } else if ("fill".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    getLayer().setFill(getInteger());
                }
            };
        } else if ("anchor".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    getLayer().setAnchor(getInteger());
                }
            };
        } else {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    public void children() throws IOException {
        super.children();

        new IntegerHandler(getWriter(), "width", getLayer().getWidth()).start();
        new IntegerHandler(getWriter(), "height", getLayer().getHeight())
                .start();
        new InsetsHandler(getWriter(), "padding", getLayer().getPadding())
                .start();
        new IntegerHandler(getWriter(), "fill", getLayer().getFill()).start();
        new IntegerHandler(getWriter(), "anchor", getLayer()
                .getAnchor()).start();
    }
}
