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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jorgan.disposition.Displayable;
import jorgan.gui.console.View;

public class SpreadHorizontalLayout extends ViewLayout {

	private int x;

	private int width;

	private int count;

	@Override
	protected void init(View<? extends Displayable> pressed,
			List<View<? extends Displayable>> views) {

		Collections.sort(views, new Comparator<View<? extends Displayable>>() {
			public int compare(View<? extends Displayable> view1,
					View<? extends Displayable> view2) {
				return view1.getX() - view2.getX();
			}
		});
		count = views.size();

		View<? extends Displayable> left = views.get(0);
		View<? extends Displayable> right = views.get(views.size() - 1);

		x = left.getX() + left.getWidth() / 2;
		width = right.getX() + right.getWidth() / 2 - x;
	}

	@Override
	public boolean isAlign() {
		return false;
	}
	
	@Override
	protected void visit(View<? extends Displayable> view, int index) {
		changePosition(view, x + (width * index / (count - 1))
				- view.getWidth() / 2, view.getY());
	}
}
