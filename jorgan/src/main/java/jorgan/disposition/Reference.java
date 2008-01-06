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
 * A reference to another element.
 */
public class Reference implements Cloneable {

	/**
	 * The referenced element.
	 */
	private Element element;

	public Reference(Element element) {
		if (element == null) {
			throw new IllegalArgumentException("element must not be null");
		}
		this.element = element;
	}

	public Element getElement() {
		return element;
	}

	/**
	 * All references are cloneable.
	 */
	@Override
	public Reference clone() {
		try {
			return (Reference) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new Error(ex);
		}
	}
	
	public Reference clone(Element element) {
		Reference clone = clone();
		
		clone.element = element;
		
		return clone;
	}
}