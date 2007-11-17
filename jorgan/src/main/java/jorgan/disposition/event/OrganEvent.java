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
package jorgan.disposition.event;

import java.util.EventObject;

import jorgan.disposition.Element;
import jorgan.disposition.Organ;

/**
 * Event describing the change of an organ.
 */
public class OrganEvent extends EventObject {

	/**
	 * Does this event indicate a disposition change.
	 */
	private boolean dispositionChange;

	/**
	 * The the element that was changed, added or removed.
	 */
	private Element element;

	/**
	 * Name of change.
	 */
	private String name;

	/**
	 * Value of change.
	 */
	private Object value;

	/**
	 * Create a new event in case of a change of a reference.
	 * 
	 * @param organ
	 *            the organ that is the source of this event
	 * @param element
	 *            the owning element of the reference
	 * @param name
	 *            name of change
	 * @param value
	 *            value of change
	 */
	public OrganEvent(Organ organ, Element element, String name, Object value,
			boolean dispositionChange) {
		super(organ);

		this.element = element;
		this.name = name;
		this.value = value;
		this.dispositionChange = dispositionChange;
	}

	/**
	 * Get the element.
	 * 
	 * @return the element
	 */
	public Element getElement() {
		return element;
	}

	/**
	 * Get the name
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the value
	 * 
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	public boolean isDispositionChange() {
		return dispositionChange;
	}
}