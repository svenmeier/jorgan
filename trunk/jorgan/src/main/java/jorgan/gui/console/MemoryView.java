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
package jorgan.gui.console;

import java.awt.Insets;
import java.text.DecimalFormat;

import jorgan.disposition.Memory;
import jorgan.skin.Layer;
import jorgan.skin.SliderLayer;
import jorgan.skin.Style;
import jorgan.skin.TextLayer;

/**
 * A view that shows a {@link Memory}.
 */
public class MemoryView extends ContinuousView {

    public static final String TEXT_POSITION = "position";

    public static final String TEXT_TITLE = "title";

    private DecimalFormat format = new DecimalFormat("000");

    public MemoryView(Memory memory) {
        super(memory);
    }

    protected Memory getMemory() {
        return (Memory) getElement();
    }

    protected void initTexts() {
        super.initTexts();
            
        setText(TEXT_POSITION, format.format(getMemory().getPosition() + 1));
        setText(TEXT_TITLE, getMemory().getTitle());
    }

    protected Style createDefaultStyle() {
        Style style = new Style();

        style.addChild(createTextLayer());
        style.addChild(createSliderLayer());

        return style;
    }

    protected TextLayer createTextLayer() {
        TextLayer layer = new TextLayer();
        layer.setText(TEXT_POSITION);
        layer.setPadding(new Insets(4, 4, 4, 4));
        layer.setFont(getDefaultFont());
        layer.setColor(getDefaultColor());

        return layer;
    }

    protected SliderLayer createSliderLayer() {
        SliderLayer layer = new SliderLayer();
        layer.setFill(Layer.BOTH);
        layer.setPadding(new Insets(4, 4, 4, 4));

        return layer;
    }
}