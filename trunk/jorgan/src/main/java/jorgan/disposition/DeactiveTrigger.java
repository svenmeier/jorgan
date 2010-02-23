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
 * An trigger of {@link jorgan.disposition.Switch}es.
 */
public class DeactiveTrigger extends Element implements Observer {

	@Override
	protected boolean canReference(Class<? extends Element> clazz) {
		return Switch.class.isAssignableFrom(clazz);
	}

	@Override
	public void changed(Element element) {
		if (!references((Element) element)) {
			throw new IllegalArgumentException("does not reference '" + element
					+ "'");
		}

		Switch first = (Switch) getReference(0).getElement();
		if (first != element && !((Switch) element).isActive()) {
			first.activate(true);
		}
	}
}