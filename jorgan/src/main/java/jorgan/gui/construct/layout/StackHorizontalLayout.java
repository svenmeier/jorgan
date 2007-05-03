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
package jorgan.gui.construct.layout;

import java.util.List;

import jorgan.gui.console.View;

public class StackHorizontalLayout extends ViewLayout {

	private int grid;

	private int x;

	private int y;

	public StackHorizontalLayout(int grid) {
		this.grid = grid;
	}

	protected void init(View pressed, List<View> views) {

		x = pressed.getX() - (x % grid);
		y = pressed.getY() - (y % grid);
	}

	protected void visit(View view, int index) {
		changePosition(view, x, y);

		int width = view.getWidth();
		x += (width - (width % grid)) + grid;
	}
}
