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
import java.awt.Rectangle;

import jorgan.gui.console.ContinuousView;

/**
 * Style.
 */
public class SliderLayer extends CompositeLayer implements Cloneable {

	public static final int DIRECTION_LEFT_RIGHT = 1;

	public static final int DIRECTION_RIGHT_LEFT = 2;

	public static final int DIRECTION_TOP_BOTTOM = 3;

	public static final int DIRECTION_BOTTOM_TOP = 4;

	private int direction = DIRECTION_LEFT_RIGHT;

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}

	@Override
	protected void drawChildren(Graphics2D g, Dimension dimension) {
		if (getChildCount() > 0) {
			double position = 0.0d;

			if (view instanceof ContinuousView) {
				position = ((ContinuousView) view).getSliderPosition();
			}

			int index;
			if (getChildCount() == 2) {
				// use first layer for position 0 only
				index = position == 0.0d ? 0 : 1;
			} else {
				index = (int) Math.round((getChildCount() - 1) * position);
			}

			Layer layer = getChild(index);

			layer.draw(g, dimension);
		}
	}

	@Override
	public void mousePressed(int x, int y, Dimension size) {
		mouseDragged(x, y, size);
	}

	@Override
	public void mouseDragged(int x, int y, Dimension size) {
		Rectangle rectangle = getUnpaddedBounds(size);

		double position = 0.0d;

		switch (direction) {
		case DIRECTION_LEFT_RIGHT:
			position = (double) (x - rectangle.x) / rectangle.width;
			break;
		case DIRECTION_RIGHT_LEFT:
			position = (double) (rectangle.width - (x - rectangle.x))
					/ rectangle.width;
			break;
		case DIRECTION_TOP_BOTTOM:
			position = (double) (y - rectangle.y) / rectangle.height;
			break;
		case DIRECTION_BOTTOM_TOP:
			position = (double) (rectangle.height - (y - rectangle.y))
					/ rectangle.height;
			break;
		}

		if (view instanceof ContinuousView) {
			position = Math.max(0.0d, position);
			position = Math.min(1.0d, position);
			((ContinuousView) view).sliderPositioned(position);
		}
	}

	@Override
	public void mouseReleased(int x, int y, Dimension size) {
		if (view instanceof ContinuousView) {
			((ContinuousView) view).sliderReleased();
		}
	}

	@Override
	public Object clone() {
		return super.clone();
	}
}