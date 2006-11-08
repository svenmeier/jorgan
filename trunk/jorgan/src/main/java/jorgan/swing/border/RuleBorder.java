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
package jorgan.swing.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 * A class that implements a border in form of a rule.
 */
public class RuleBorder implements Border {

	/**
	 * Paint rule to the top of the component.
	 * 
	 * @see #RuleBorder(int, boolean, boolean)
	 */
	public static final int TOP = SwingConstants.TOP;

	/**
	 * Paint rule to the bottom of the component.
	 * 
	 * @see #RuleBorder(int, boolean, boolean)
	 */
	public static final int BOTTOM = SwingConstants.BOTTOM;

	/**
	 * Paint rule to the left of the component.
	 * 
	 * @see #RuleBorder(int, boolean, boolean)
	 */
	public static final int LEFT = SwingConstants.LEFT;

	/**
	 * Paint rule to the right of the component.
	 * 
	 * @see #RuleBorder(int, boolean, boolean)
	 */
	public static final int RIGHT = SwingConstants.RIGHT;

	private int location;

	/**
	 * Constructor.
	 */
	public RuleBorder() {
		this(BOTTOM);
	}

	/**
	 * Constructor.
	 * 
	 * @param location
	 *            location of rule
	 * @see #RuleBorder(int, boolean, boolean)
	 */
	public RuleBorder(int location) {
		this(location, true, true);
	}

	/**
	 * Constructor.
	 * 
	 * @param location
	 *            location of rule, {@link #TOP}, {@link #BOTTOM},
	 *            {@link #LEFT} or {@link #RIGHT}
	 * @param paintHighlight
	 *            should highlight be painted
	 * @param paintShadow
	 *            should shadow be painted
	 */
	public RuleBorder(int location, boolean paintHighlight, boolean paintShadow) {
		this.location = location;
	}

	/**
	 * Paint.
	 */
	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {

		switch (location) {
		case TOP:
			g.setColor(getShadowColor(c));
			g.drawLine(x, y, x + width - 1, y);

			g.setColor(getHighlightColor(c));
			g.drawLine(x, y + 1, x + width - 1, y + 1);
			break;
		case LEFT:
			g.setColor(getShadowColor(c));
			g.drawLine(x, y, x, y + height - 1);

			g.setColor(getHighlightColor(c));
			g.drawLine(x + 1, y, x + 1, y + height - 1);
			break;
		case BOTTOM:
			g.setColor(getShadowColor(c));
			g.drawLine(x, y + height - 2, x + width - 1, y + height - 2);

			g.setColor(getHighlightColor(c));
			g.drawLine(x, y + height - 1, x + width - 1, y + height - 1);
			break;
		case RIGHT:
			g.setColor(getShadowColor(c));
			g.drawLine(x + width - 2, y, x + width - 2, y + height - 1);

			g.setColor(getHighlightColor(c));
			g.drawLine(x + width - 1, y, x + width - 1, y + height - 1);
			break;
		}
	}

	public Insets getBorderInsets(Component c) {
		switch (location) {
		case TOP:
			return new Insets(2, 0, 0, 0);
		case LEFT:
			return new Insets(0, 2, 0, 0);
		case BOTTOM:
			return new Insets(0, 0, 2, 0);
		case RIGHT:
			return new Insets(0, 0, 0, 2);
		}
		return new Insets(0, 0, 0, 0);
	}

	public boolean isBorderOpaque() {
		return true;
	}

	/**
	 * Returns the highlight color of the etched border when rendered on the
	 * specified component.
	 * 
	 * @param c
	 *            the component for which the highlight shall be derived
	 * @return the color to highlight with
	 */
	public Color getHighlightColor(Component c) {
		return c.getBackground().brighter();
	}

	/**
	 * Returns the shadow color of the etched border when rendered on the
	 * specified component.
	 * 
	 * @param c
	 *            the component for which the shadow shall be derived
	 * @return the color to use as shadow
	 */
	public Color getShadowColor(Component c) {
		return c.getBackground().darker();
	}
}
