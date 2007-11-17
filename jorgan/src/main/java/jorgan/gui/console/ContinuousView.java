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
import java.text.NumberFormat;

import jorgan.disposition.Continuous;
import jorgan.skin.Layer;
import jorgan.skin.SliderLayer;
import jorgan.skin.Style;
import jorgan.skin.TextLayer;

/**
 * A view for a continuous.
 */
public class ContinuousView<E extends Continuous> extends View<E> {

	public static final String BINDING_VALUE = "value";

	private NumberFormat valueFormat = new DecimalFormat("0.0");

	/**
	 * Constructor.
	 * 
	 * @param continuous
	 *            continuous to view
	 */
	public ContinuousView(E continuous) {
		super(continuous);
	}

	@Override
	protected void initBindings() {
		super.initBindings();

		setBinding(BINDING_VALUE, new ValueBindings());
	}

	@Override
	protected Style createDefaultStyle() {
		Style style = new Style();

		style.addChild(createTextNameLayer());
		style.addChild(createTextValueLayer());
		style.addChild(createSliderLayer());

		return style;
	}

	protected TextLayer createTextNameLayer() {
		TextLayer layer = new TextLayer();
		layer.setBinding(CONTROL_NAME);
		layer.setPadding(new Insets(4, 4, 4 + getDefaultFont().getSize(), 4));
		layer.setFont(getDefaultFont());
		layer.setColor(getDefaultColor());

		return layer;
	}

	protected TextLayer createTextValueLayer() {
		TextLayer layer = new TextLayer();
		layer.setBinding(BINDING_VALUE);
		layer.setPadding(new Insets(4 + getDefaultFont().getSize(), 4, 4, 4));
		layer.setFont(getDefaultFont());
		layer.setColor(getDefaultColor());

		return layer;
	}

	protected SliderLayer createSliderLayer() {
		SliderLayer layer = new SliderLayer();
		layer.setBinding(BINDING_VALUE);
		layer.setFill(Layer.BOTH);
		layer.setPadding(new Insets(4, 4, 4, 4));

		return layer;
	}
	
	private class ValueBindings implements TextLayer.Binding, SliderLayer.Binding {
		
		public boolean isPressable() {
			return true;
		}
		
		public String getText() {
			return valueFormat.format(getElement().getValue());
		}

		public double getPosition() {
			return getElement().getValue() / 127.0d;
		}

		public void setPosition(float position) {
			getElement().setValue(position);
		}

		public void released() {
			if (!getElement().isLocking()) {
				getElement().setValue(0);
			}
		}
	};
}