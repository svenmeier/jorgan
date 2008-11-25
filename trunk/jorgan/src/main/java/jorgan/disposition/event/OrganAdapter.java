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

import jorgan.disposition.Element;
import jorgan.disposition.Message;
import jorgan.disposition.Reference;

/**
 * A default implementation of a listener to organ changes.
 */
public class OrganAdapter implements OrganListener {

	public void elementAdded(Element element) {
	}

	public void elementRemoved(Element element) {
	}

	public void messageAdded(Element element, Message reference) {
	}

	public void messageChanged(Element element, Message reference) {
	}

	public void messageRemoved(Element element, Message reference) {
	}

	public void propertyChanged(Element element, String name) {
	}

	public void referenceAdded(Element element, Reference<?> reference) {
	}

	public void referenceChanged(Element element, Reference<?> reference) {
	}

	public void referenceRemoved(Element element, Reference<?> reference) {
	}

}