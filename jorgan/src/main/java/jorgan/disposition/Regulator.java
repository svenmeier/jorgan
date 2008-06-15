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
public class Regulator extends IndexedContinuous implements Engaging {

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
	public void engagedChanged(Engageable element, boolean engaged) {
		if (!references((Element) element)) {
			throw new IllegalArgumentException("does not reference '" + element
					+ "'");
		}

		if (engaged) {
			if (getReference(getIndex()).getElement() != element) {
				setIndex(getReferencedIndex(element));
			}
		}
	}

	@Override
	protected void onIndexChanged(int oldIndex, int newIndex) {
		// to minimuze switching first engage ..
		((Switch) getReference(newIndex).getElement()).engagingChanged(true);
		// .. then disengage
		((Switch) getReference(oldIndex).getElement()).engagingChanged(false);
	}
}