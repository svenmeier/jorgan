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

import jorgan.skin.CompositeLayer;
import jorgan.skin.ImageLayer;
import jorgan.skin.Layer;
import jorgan.skin.TextLayer;
import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;

import org.xml.sax.Attributes;

public class CompositeLayerHandler extends LayerHandler {

    private CompositeLayer layer;

    /**
     * Constructor.
     */
    public CompositeLayerHandler(AbstractReader reader) {
        super(reader);

        layer = createLayer();
    }

    protected CompositeLayer createLayer() {
        return new CompositeLayer();
    }

    public CompositeLayerHandler(AbstractWriter writer, String tag,
            CompositeLayer layer) {
        super(writer, tag);

        this.layer = layer;
    }

    public Layer getLayer() {
        return getCompositeLayer();
    }

    public CompositeLayer getCompositeLayer() {
        return layer;
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {

        if ("composite".equals(qName)) {
            new CompositeLayerHandler(getReader()) {
                public void finished() {
                    layer.addChild(getLayer());
                }
            };
        } else if ("image".equals(qName)) {
            new ImageLayerHandler(getReader()) {
                public void finished() {
                    layer.addChild(getLayer());
                }
            };
        } else if ("text".equals(qName)) {
            new TextLayerHandler(getReader()) {
                public void finished() {
                    layer.addChild(getLayer());
                }
            };
        } else if ("button".equals(qName)) {
            new ButtonLayerHandler(getReader()) {
                public void finished() {
                    layer.addChild(getLayer());
                }
            };
        } else if ("slider".equals(qName)) {
            new SliderLayerHandler(getReader()) {
                public void finished() {
                    layer.addChild(getLayer());
                }
            };
        } else {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    public void children() throws IOException {
        super.children();

        for (int l = 0; l < layer.getChildCount(); l++) {
            Layer child = layer.getChild(l);

            if (child instanceof CompositeLayer) {
                new CompositeLayerHandler(getWriter(), "composite",
                        (CompositeLayer) child).start();
            } else if (child instanceof ImageLayer) {
                new ImageLayerHandler(getWriter(), "image", (ImageLayer) child)
                        .start();
            } else if (child instanceof TextLayer) {
                new TextLayerHandler(getWriter(), "text", (TextLayer) child)
                        .start();
            }
        }
    }
}
