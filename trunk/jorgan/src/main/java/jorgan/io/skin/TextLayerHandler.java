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
import jorgan.skin.TextLayer;
import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;
import jorgan.xml.handler.BooleanHandler;
import jorgan.xml.handler.IntegerHandler;
import jorgan.xml.handler.StringHandler;

import org.xml.sax.Attributes;

public class TextLayerHandler extends LayerHandler {

    private TextLayer layer;

    /**
     * Constructor.
     */
    public TextLayerHandler(AbstractReader reader) {
        super(reader);

        this.layer = new TextLayer();
    }

    public TextLayerHandler(AbstractWriter writer, String tag, TextLayer layer) {
        super(writer, tag);

        this.layer = layer;
    }

    public TextLayer getTextLayer() {
        return layer;
    }

    public Layer getLayer() {
        return getTextLayer();
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {

        if ("color".equals(qName)) {
            new ColorHandler(getReader()) {
                public void finished() {
                    layer.setColor(getColor());
                }
            };
        } else if ("font".equals(qName)) {
            new FontHandler(getReader()) {
                public void finished() {
                    layer.setFont(getFont());
                }
            };
        } else if ("antialiased".equals(qName)) {
            new BooleanHandler(getReader()) {
                public void finished() {
                    layer.setAntialiased(getBoolean());
                }
            };
        } else if ("horizontalAlignment".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    layer.setHorizontalAlignment(getInteger());
                }
            };
        } else if ("verticalAlignment".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    layer.setVerticalAlignment(getInteger());
                }
            };
        } else if ("text".equals(qName)) {
            new StringHandler(getReader()) {
                public void finished() {
                    layer.setText(getString());
                }
            };
        } else {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    public void children() throws IOException {
        super.children();

        new ColorHandler(getWriter(), "color", layer.getColor()).start();
        new FontHandler(getWriter(), "font", layer.getFont()).start();
        new BooleanHandler(getWriter(), "antialiased", layer.isAntialiased())
                .start();
        new IntegerHandler(getWriter(), "horizontalAlignment", layer
                .getHorizontalAlignment()).start();
        new IntegerHandler(getWriter(), "verticalAlignment", layer
                .getVerticalAlignment()).start();
        new StringHandler(getWriter(), "text", layer.getText()).start();
    }
}
