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
			float position = 0.0f;

			Binding binding = getBinding(Binding.class);
			if (binding != null) {
				position = binding.getPosition();
			}

			int index;
			if (getChildCount() == 2) {
				// use first layer for position 0 only
				index = position == 0.0f ? 0 : 1;
			} else {
				index = Math.round((getChildCount() - 1) * position);
			}

			Layer layer = getChild(index);

			layer.draw(g, dimension);
		}
	}

	@Override
	protected void mousePressed(int x, int y, Rectangle bounds) {
		mouseDragged(x, y, bounds);
	}

	@Override
	protected void mouseDragged(int x, int y, Rectangle bounds) {
		float position = 0.0f;

		switch (direction) {
		case DIRECTION_LEFT_RIGHT:
			position = (float) (x - bounds.x) / bounds.width;
			break;
		case DIRECTION_RIGHT_LEFT:
			position = (float) (bounds.width - (x - bounds.x)) / bounds.width;
			break;
		case DIRECTION_TOP_BOTTOM:
			position = (float) (y - bounds.y) / bounds.height;
			break;
		case DIRECTION_BOTTOM_TOP:
			position = (float) (bounds.height - (y - bounds.y)) / bounds.height;
			break;
		}

		position = Math.max(0.0f, position);
		position = Math.min(1.0f, position);
		getBinding(Binding.class).setPosition(position);
	}

	@Override
	protected void mouseReleased(int x, int y, Rectangle bounds) {
		getBinding(Binding.class).released();
	}

	@Override
	public Object clone() {
		return super.clone();
	}

	public static interface Binding extends ViewBinding {
		public float getPosition();

		public void setPosition(float position);

		public void released();
	}
}