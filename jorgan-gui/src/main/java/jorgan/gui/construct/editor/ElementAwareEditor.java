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
package jorgan.gui.construct.editor;

import jorgan.disposition.Element;
import jorgan.session.OrganSession;

/**
 * Editor that is aware of the element of the edited property.
 */
public interface ElementAwareEditor {

	/**
	 * @deprecated override {@link #setElement(OrganSession, Element) instead
	 */
	public default void setElement(Element element) {
	}

	public default void setElement(OrganSession session, Element element) {
		setElement(element);
	}
}
