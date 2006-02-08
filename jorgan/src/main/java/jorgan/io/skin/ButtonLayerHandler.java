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

import jorgan.skin.ButtonLayer;
import jorgan.skin.CompositeLayer;
import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;

public class ButtonLayerHandler extends CompositeLayerHandler {

    /**
     * Constructor.
     */
    public ButtonLayerHandler(AbstractReader reader) {
        super(reader);
    }

    protected CompositeLayer createLayer() {
        return new ButtonLayer();
    }

    public ButtonLayerHandler(AbstractWriter writer, String tag,
            ButtonLayer buttonLayer) {
        super(writer, tag, buttonLayer);
    }

    public ButtonLayer getButtonLayer() {
        return (ButtonLayer) getLayer();
    }
}