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

import java.awt.Point;
import java.util.List;

import javax.swing.Icon;

import jorgan.disposition.Displayable;
import jorgan.gui.console.View;
import bias.Configuration;

/**
 * The Layout for views.
 */
public abstract class ViewLayout {

	private String name;

	private Icon icon;

	protected ViewLayout() {
		Configuration config = Configuration.getRoot().get(getClass());

		config.read(this);
	}

	/**
	 * Get the name of this layout.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the icon of this layout.
	 * 
	 * @return icon
	 */
	public Icon getIcon() {
		return icon;
	}

	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Layout the given views.
	 * 
	 * @param pressed
	 *            the pressed view
	 * @param views
	 *            the selected views to layout
	 */
	public void layout(View<? extends Displayable> pressed,
			List<View<? extends Displayable>> views) {
		init(pressed, views);

		for (int s = 0; s < views.size(); s++) {
			View<? extends Displayable> view = views.get(s);
			visit(view, s);
		}
	}

	protected void changePosition(View<? extends Displayable> view, int x, int y) {
		view.getContainer().setLocation(view, new Point(x, y));
	}

	protected void init(View<? extends Displayable> pressed,
			List<View<? extends Displayable>> views) {
	}

	protected void visit(View<? extends Displayable> view, int index) {
	}
}