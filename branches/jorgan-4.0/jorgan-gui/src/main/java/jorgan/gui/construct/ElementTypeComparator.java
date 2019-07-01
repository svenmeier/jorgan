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
package jorgan.gui.construct;

import java.util.Comparator;

import jorgan.disposition.Element;
import jorgan.disposition.Elements;

/**
 * Comparator of elements.
 */
public class ElementTypeComparator implements Comparator<Element> {

	public int compare(Element e1, Element e2) {
		return Elements.getDisplayName(e1.getClass()).compareTo(
				Elements.getDisplayName(e2.getClass()));
	}
}