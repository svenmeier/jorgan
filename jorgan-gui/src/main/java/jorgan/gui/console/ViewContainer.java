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

import java.awt.Component;
import java.awt.Point;

import javax.swing.JComponent;

import jorgan.disposition.Console;
import jorgan.disposition.Displayable;
import jorgan.skin.Style;

/**
 * Container of {@link View}s.
 */
public interface ViewContainer {

	/**
	 * Get the hosting {@link Component}.
	 */
	public Component getHost();

	public float getScale(View<? extends Displayable> view);

	/**
	 * Get the style for the given view.
	 */
	public Style getStyle(View<? extends Displayable> view);

	/**
	 * Get the location for the given view.
	 */
	public Point getLocation(View<? extends Displayable> view);

	/**
	 * Set the location for the given view.
	 */
	public void setLocation(View<? extends Displayable> view, Point location);

	public void showPopup(View<? extends Displayable> view, JComponent contents);

	public void hidePopup();

	public void toFront(Console console);
}