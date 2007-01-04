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

import java.awt.Font;
import java.awt.Insets;
import java.text.DecimalFormat;

import jorgan.disposition.Continuous;
import jorgan.skin.Layer;
import jorgan.skin.SliderLayer;
import jorgan.skin.Style;
import jorgan.skin.TextLayer;

/**
 * A view for a continuous.
 */
public class ContinuousView extends View {

    public static final String TEXT_VALUE = "value";

    private DecimalFormat format = new DecimalFormat("000");

    public ContinuousView(Continuous continuous) {
        super(continuous);
    }

    protected Continuous getContinuous() {
        return (Continuous) getElement();
    }

    protected void initTexts() {
        super.initTexts();

        setText(TEXT_VALUE, format.format(getContinuous().getValue() + 1));
    }

    public int getSliderMove() {
        return getContinuous().getValue();
    }

    public void sliderMoved(int value) {
        getContinuous().setValue(value);
    }

    public void sliderReleased() {
        if (!getContinuous().isLocking()) {
            getContinuous().setValue(0);
        }
    }

    protected Style createDefaultStyle() {
        Style style = new Style();

        style.addChild(createTextNameLayer());
        style.addChild(createTextValueLayer());
        style.addChild(createSliderLayer());

        return style;
    }

    protected TextLayer createTextNameLayer() {
        Font font = Configuration.instance().getFont();
        
        TextLayer layer = new TextLayer();
        layer.setText("${" + TEXT_NAME + "}");
        layer.setPadding(new Insets(4, 4, 4 + font.getSize(), 4));
        layer.setFont(font);
        layer.setColor(getDefaultColor());

        return layer;
    }

    protected TextLayer createTextValueLayer() {
        Font font = Configuration.instance().getFont();
        
        TextLayer layer = new TextLayer();
        layer.setText("${" + TEXT_VALUE + "}");
        layer.setPadding(new Insets(4 + font.getSize(), 4, 4, 4));
        layer.setFont(font);
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