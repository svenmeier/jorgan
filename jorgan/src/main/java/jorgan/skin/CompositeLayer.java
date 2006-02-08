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
package jorgan.skin;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import jorgan.gui.console.View;

/**
 * A composition of layers.
 */
public class CompositeLayer extends Layer {

    private List layers = new ArrayList();

    public List getLayers() {
        return layers;
    }

    public void addChild(Layer layer) {
        layers.add(layer);
        layer.setResolver(this);
    }

    public int getChildCount() {
        return layers.size();
    }

    public Layer getChild(int index) {
        return (Layer) layers.get(index);
    }

    public Dimension getSize() {
        Dimension dimension = super.getSize();

        for (int l = 0; l < layers.size(); l++) {
            Layer layer = (Layer) layers.get(l);

            Dimension dim = layer.getSize();
            dimension.width = Math.max(dimension.width, dim.width);
            dimension.height = Math.max(dimension.height, dim.height);
        }

        return dimension;
    }

    public void init(View view, Component component) {
        super.init(view, component);

        for (int l = 0; l < layers.size(); l++) {
            Layer layer = (Layer) layers.get(l);

            layer.init(view, component);
        }
    }

    public boolean isPressable(int x, int y, Dimension dimension) {
        if (super.isPressable(x, y, dimension)) {
            return true;
        }
        for (int l = 0; l < layers.size(); l++) {
            Layer layer = (Layer) layers.get(l);

            if (layer.isPressable(x, y, dimension)) {
                return true;
            }
        }
        return false;
    }

    public void mousePressed(int x, int y, Dimension size) {
        super.mousePressed(x, y, size);

        for (int l = 0; l < layers.size(); l++) {
            Layer layer = (Layer) layers.get(l);

            layer.mousePressed(x, y, size);
        }
    }

    public void mouseDragged(int x, int y, Dimension size) {
        super.mouseDragged(x, y, size);

        for (int l = 0; l < layers.size(); l++) {
            Layer layer = (Layer) layers.get(l);

            layer.mouseDragged(x, y, size);
        }
    }

    public void mouseReleased(int x, int y, Dimension size) {
        super.mouseReleased(x, y, size);

        for (int l = 0; l < layers.size(); l++) {
            Layer layer = (Layer) layers.get(l);

            layer.mouseReleased(x, y, size);
        }
    }

    public void draw(Graphics2D g, Dimension dimension) {
        super.draw(g, dimension);

        drawChildren(g, dimension);
    }

    protected void drawChildren(Graphics2D g, Dimension dimension) {
        for (int l = 0; l < layers.size(); l++) {
            Layer layer = (Layer) layers.get(l);

            layer.draw(g, dimension);
        }
    }

    public Object clone() {
        CompositeLayer clone = (CompositeLayer) super.clone();

        clone.layers = new ArrayList();

        for (int l = 0; l < layers.size(); l++) {
            Layer layer = getChild(l);

            clone.addChild((Layer) layer.clone());
        }

        return clone;
    }
}