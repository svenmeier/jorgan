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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;

import jorgan.disposition.Continuous;
import jorgan.skin.Layer;
import jorgan.skin.SliderLayer;
import jorgan.skin.Style;
import jorgan.skin.TextLayer;

/**
 * A view for a continuous.
 */
public class ContinuousView extends View {

    public ContinuousView(Continuous continuous) {
        super(continuous);
    }

    protected Continuous getContinuous() {
        return (Continuous) getElement();
    }

    public int getPosition() {
        return getContinuous().getPosition();
    }

    public void setPosition(int position) {
        getContinuous().setPosition(position);
    }

    public void released() {
        if (!getContinuous().isLocking()) {
            getContinuous().setPosition(0);
        }
    }
    
    protected Style createDefaultStyle() {
        Style style = new Style();

        style.addChild(createSliderLayer());
        style.addChild(createTextLayer());

        return style;
    }

    protected TextLayer createTextLayer() {
        TextLayer layer = new TextLayer();
        layer.setText(TEXT_NAME);
        layer.setPadding(new Insets(4, 4, 4 + 13 + 4, 4));
        layer.setVerticalAnchor(Layer.TRAILING);
        layer.setFont(getDefaultFont());
        layer.setColor(getDefaultColor());

        return layer;
    }

    protected SliderLayer createSliderLayer() {
        SliderLayer layer = new SliderLayer() {
            protected void draw(Graphics2D g, int x, int y, int width,
                    int height) {

                g.setColor(Color.black);

                g.drawRect(x, y, width - 1, height - 1);

                g.fillRect(x + 2, y + 2, getPosition() * 76 / 127, 9);
            }
        };
        layer.setWidth(80);
        layer.setHeight(13);
        layer.setPadding(new Insets(4, 4, 4, 4));
        layer.setVerticalAnchor(Layer.TRAILING);

        return layer;
    }
}