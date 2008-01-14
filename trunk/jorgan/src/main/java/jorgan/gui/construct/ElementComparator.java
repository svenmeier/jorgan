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
import java.util.List;

import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.disposition.Organ;

/**
 * Comparator of elements.
 */
public class ElementComparator implements Comparator<Element> {

	private static List<Class<? extends Element>> types = Organ
			.getElementClasses();

	private boolean alphabet;

	public ElementComparator(boolean alphabet) {
		this.alphabet = alphabet;
	}

	public int compare(Element e1, Element e2) {

		if (alphabet) {
			int result = compareByName(e1, e2);
			if (result == 0) {
				result = compareByType(e1, e2);
			}
			return result;
		} else {
			int result = compareByType(e1, e2);
			if (result == 0) {
				result = compareByName(e1, e2);
			}
			return result;
		}
	}

	public static int compareByName(Element e1, Element e2) {

		return Elements.getDisplayName(e1).compareTo(Elements.getDisplayName(e2));
	}

	public static int compareByType(Element e1, Element e2) {

		int index1 = types.indexOf(e1.getClass());
		int index2 = types.indexOf(e2.getClass());

		return index1 - index2;
	}
}