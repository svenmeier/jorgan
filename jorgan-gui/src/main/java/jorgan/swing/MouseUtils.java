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
package jorgan.swing;

import java.awt.event.MouseEvent;

/**
 * Utility method for mouse.
 */
public class MouseUtils {
	/**
	 * Swing does not support horizontal touch scrolling, button-press events
	 * are generated for back (button 4) and forward (button 5) instead.
	 * <p>
	 * These have to be ignored.
	 * 
	 * @param ev
	 */
	public static boolean isHorizontalScroll(MouseEvent ev) {
		return ev.getButton() > MouseEvent.BUTTON3;
	}
}