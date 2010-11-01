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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jorgan.disposition.Displayable;
import jorgan.gui.console.View;

/**
 * A composition of layers.
 */
public class CompositeLayer extends Layer {

	private List<Layer> layers = new ArrayList<Layer>();

	public List<Layer> getLayers() {
		return layers;
	}

	public void addChild(Layer layer) {
		layers.add(layer);
	}

	public int getChildCount() {
		return layers.size();
	}

	public Layer getChild(int index) {
		return layers.get(index);
	}

	public List<Layer> getChildren() {
		return Collections.unmodifiableList(layers);
	}

	@Override
	public Dimension getSize() {
		Dimension dimension = super.getSize();

		for (int l = 0; l < layers.size(); l++) {
			Layer layer = layers.get(l);

			Dimension dim = layer.getSize();
			dimension.width = Math.max(dimension.width, dim.width);
			dimension.height = Math.max(dimension.height, dim.height);
		}

		return dimension;
	}

	@Override
	public void setView(View<? extends Displayable> view) {
		super.setView(view);

		for (int l = 0; l < layers.size(); l++) {
			Layer layer = layers.get(l);

			layer.setView(view);
		}
	}

	@Override
	public Layer getPressable(int x, int y, Dimension dimension) {
		Layer pressable = super.getPressable(x, y, dimension);
		if (pressable != null) {
			return pressable;
		}

		for (int l = 0; l < layers.size(); l++) {
			Layer layer = layers.get(l);

			pressable = layer.getPressable(x, y, dimension);
			if (pressable != null) {
				return pressable;
			}
		}

		return null;
	}

	@Override
	public void draw(Graphics2D g, Dimension dimension) {
		super.draw(g, dimension);

		drawChildren(g, dimension);
	}

	protected void drawChildren(Graphics2D g, Dimension dimension) {
		for (int l = 0; l < layers.size(); l++) {
			Layer layer = layers.get(l);

			layer.draw(g, dimension);
		}
	}

	@Override
	public Object clone() {
		CompositeLayer clone = (CompositeLayer) super.clone();

		clone.layers = new ArrayList<Layer>();

		for (int l = 0; l < layers.size(); l++) {
			Layer layer = getChild(l);

			clone.addChild((Layer) layer.clone());
		}

		return clone;
	}
}