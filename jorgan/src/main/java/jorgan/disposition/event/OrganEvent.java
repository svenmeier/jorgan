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
import jorgan.disposition.Message;
import jorgan.disposition.Organ;
import jorgan.disposition.Reference;

/**
 * Event describing the change of an organ.
 */
public class OrganEvent extends EventObject {

	/**
	 * Does this event indicate a disposition change.
	 */
	private boolean dispositionChange;

	private Element element;

	private Reference reference;

	private Message message;

	public OrganEvent(Organ organ, Element element,
			boolean dispositionChange) {
		this(organ, element, null, null, dispositionChange);
	}

	public OrganEvent(Organ organ, Element element, Reference reference, 
			boolean dispositionChange) {
		this(organ, element, reference, null, dispositionChange);
	}

	public OrganEvent(Organ organ, Element element, Message message,
			boolean dispositionChange) {
		this(organ, element, null, message, dispositionChange);
	}

	private OrganEvent(Organ organ, Element element, Reference reference, Message message,
			boolean dispositionChange) {
		super(organ);

		this.element = element;
		this.reference = reference;
		this.message = message;
		this.dispositionChange = dispositionChange;
	}
	
	public boolean isDispositionChange() {
		return dispositionChange;
	}

	public Element getElement() {
		return element;
	}

	public Reference getReference() {
		return reference;
	}

	public Message getMessage() {
		return message;
	}	
	
	public boolean self() {
		return reference == null && message == null;
	}
}