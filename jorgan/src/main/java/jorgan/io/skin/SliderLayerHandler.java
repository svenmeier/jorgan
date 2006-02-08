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
import jorgan.skin.SliderLayer;
import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;
import jorgan.xml.handler.IntegerHandler;

import org.xml.sax.Attributes;

public class SliderLayerHandler extends CompositeLayerHandler {

    /**
     * Constructor.
     */
    public SliderLayerHandler(AbstractReader reader) {
        super(reader);
    }

    protected CompositeLayer createLayer() {
        return new SliderLayer();
    }

    public SliderLayerHandler(AbstractWriter writer, String tag,
            SliderLayer sliderLayer) {
        super(writer, tag, sliderLayer);
    }

    public SliderLayer getSliderLayer() {
        return (SliderLayer) getLayer();
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {

        if ("direction".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    getSliderLayer().setDirection(getInteger());
                }
            };
        } else {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    public void children() throws IOException {
        super.children();

        new IntegerHandler(getWriter(), "direction", getSliderLayer()
                .getDirection()).start();
    }
}
