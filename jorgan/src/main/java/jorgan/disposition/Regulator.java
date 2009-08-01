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
 * An regulator of {@link jorgan.disposition.Switch}es.
 */
public class Regulator extends IndexedContinuous implements Engaging,
		Activating {

	@Override
	protected boolean canReference(Class<? extends Element> clazz) {
		return Switch.class.isAssignableFrom(clazz);
	}

	@Override
	protected boolean canReferenceDuplicates() {
		return true;
	}

	public int getSize() {
		return getReferenceCount();
	}

	public void setTitle(String title) {
		int index = getIndex();
		
		if (index < getSize()) {
			getReference(index).getElement().setName(title);
		}
	}
	
	/**
	 * A regulator enages the referenced {@link Switch} corresponding to the
	 * current {@link #getIndex()}.
	 */
	public boolean engages(Engageable element) {
		if (!references((Element) element)) {
			throw new IllegalArgumentException("does not reference '" + element
					+ "'");
		}

		return getReference(getIndex()).getElement() == element;
	}

	/**
	 * Adjust {@link #getIndex()} if corresponding referenced {@link Switch} is
	 * engaged.
	 */
	public void activeChanged(Switch element, boolean active) {
		if (!references((Element) element)) {
			throw new IllegalArgumentException("does not reference '" + element
					+ "'");
		}

		if (active) {
			if (getReference(getIndex()).getElement() != element) {
				setIndex(getReferencedIndex(element));
			}
		}
	}

	@Override
	protected void onIndexChanged(int oldIndex, int newIndex) {
		// Disengage old switch first to save resources. Note that referenced
		// switches (including further referenced switches in case of an
		// Activator) will stay engaged if they are reference by the new switch.
		((Switch) getReference(oldIndex).getElement()).engagingChanged(false);
		((Switch) getReference(newIndex).getElement()).engagingChanged(true);
	}
}