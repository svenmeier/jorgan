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
public class ContinuousView extends View {

	/**
	 * The key of the {@link Continuous#getValue()} text for {@link TextLayer}s.
	 */
	public static final String TEXT_VALUE = "value";

	/**
	 * The key of the position text for {@link TextLayer}s.
	 */
	public static final String TEXT_POSITION = "position";

	private NumberFormat valueFormat = new DecimalFormat("000");

	private NumberFormat positionFormat = NumberFormat.getPercentInstance();

	/**
	 * Constructor.
	 * 
	 * @param continuous
	 *            continuous to view
	 */
	public ContinuousView(Continuous continuous) {
		super(continuous);
	}

	protected Continuous getContinuous() {
		return (Continuous) getElement();
	}

	protected void initTexts() {
		super.initTexts();

		int value = getContinuous().getValue();

		setText(TEXT_VALUE, valueFormat.format(value + 1));
		setText(TEXT_POSITION, positionFormat.format(value / 127.0d));
	}

	/**
	 * Get the position for a {@link SliderLayer}.
	 * 
	 * @return the current position
	 */
	public double getSliderPosition() {
		return getContinuous().getValue() / 127.0d;
	}

	/**
	 * The {@link jorgan.skin.SliderLayer} was positioned.
	 * 
	 * @param position
	 *            the new position
	 */
	public void sliderPositioned(double position) {
		getContinuous().setValue((int) (position * 127));
	}

	/**
	 * The {@link jorgan.skin.SliderLayer} was released.
	 */
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
		TextLayer layer = new TextLayer();
		layer.setText("${" + TEXT_NAME + "}");
		layer.setPadding(new Insets(4, 4, 4 + getDefaultFont().getSize(), 4));
		layer.setFont(getDefaultFont());
		layer.setColor(getDefaultColor());

		return layer;
	}

	protected TextLayer createTextValueLayer() {
		TextLayer layer = new TextLayer();
		layer.setText("${" + TEXT_VALUE + "}");
		layer.setPadding(new Insets(4 + getDefaultFont().getSize(), 4, 4, 4));
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