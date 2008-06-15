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
package jorgan.disposition;

/**
 * An element that engages {@link Engageable}s.
 * 
 * @see Engageable#engagingChanged(Engaging)
 */
public interface Engaging {

	/**
	 * Is the given engageable currently engaged by this element.
	 * 
	 * @param element
	 * @see Engageable#isEngaged()
	 */
	public boolean engages(Engageable element);
	
	/**
	 * Notification about a change of {@link Engageable#isEngaged()}.
	 * 
	 * @param element
	 */
	public void engagedChanged(Engageable element, boolean engaged);
}
