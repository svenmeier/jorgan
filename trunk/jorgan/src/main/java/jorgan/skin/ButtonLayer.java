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
 * A layer for a {@link jorgan.disposition.Momentary}.
 */
public class ButtonLayer extends CompositeLayer implements Cloneable {

	@Override
	protected void drawChildren(Graphics2D g, Dimension dimension) {
		if (getChildCount() > 0) {
			int index = 0;

			Binding binding = getBinding(Binding.class);
			if (binding != null && binding.isPressed()) {
				index++;
			}

			Layer layer = getChild(Math.min(getChildCount() - 1, index));

			layer.draw(g, dimension);
		}
	}

	@Override
	protected void mousePressed(int x, int y, Rectangle bounds) {
		getBinding(Binding.class).pressed();
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

		public boolean isPressed();

		public void pressed();

		public void released();
	}
}