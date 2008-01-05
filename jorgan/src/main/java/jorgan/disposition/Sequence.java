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
 * A sequence of {@link jorgan.disposition.Combination}s.
 */
public class Sequence extends IndexedContinuous implements Combination.Observer {

	@Override
	protected boolean canReference(Class<? extends Element> clazz) {
		return Combination.class.isAssignableFrom(clazz);
	}

	@Override
	protected boolean canReferenceDuplicates() {
		return true;
	}

	public int getSize() {
		return getReferenceCount();
	}

	public void initiated(Combination combination) {
		if (getCombination() != combination) {
			int index = getReferencedIndex(combination);

			setIndex(index);
		}
	}

	public void increment(int delta) {
		super.increment(delta);

		getCombination().recall();
	}

	private Combination getCombination() {
		if (getReferenceCount() == 0) {
			return null;
		} else {
			int index = getIndex();

			Reference reference = getReference(index);
			return ((Combination) reference.getElement());
		}
	}
}