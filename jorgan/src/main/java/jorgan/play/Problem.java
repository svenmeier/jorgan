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
package jorgan.play;

import jorgan.disposition.Element;

/**
 * Problem of a player.
 */
public abstract class Problem {

	private Element element;

	private String property;

	private Object value;

	protected Problem(Element element, String property, Object value) {
		this.element = element;
		this.property = property;
		this.value = value;
	}

	public String getProperty() {
		return property;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || !(object.getClass() == this.getClass())) {
			return false;
		}

		Problem problem = (Problem) object;

		if (!(this.element == problem.element)) {
			return false;
		}
		
		if (!this.property.equals(problem.property)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		return element.hashCode();
	}

	@Override
	public String toString() {
		String name = getClass().getName();
		return name.substring(name.lastIndexOf('.') + 1) + "." + property;
	}

	public Element getElement() {
		return element;
	}
}