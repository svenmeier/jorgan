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

import java.util.Arrays;


public class Combination extends Initiator {

	@Override
	protected boolean canReference(Class clazz) {
		return Activateable.class.isAssignableFrom(clazz);
	}

	@Override
	protected Reference createReference(Element element) {
		return new CombinationReference((Activateable) element);
	}

	@Override
	public void initiate() {
		for (Captor captor : getReferrer(Captor.class)) {
			if (captor.isActive()) {
				capture();
				return;
			}
		}

		recall();
	}

	public void recall() {

		int index = getIndex();

		for (int e = 0; e < getReferenceCount(); e++) {
			CombinationReference reference = (CombinationReference) getReference(e);

			Activateable registratable = reference.getRegistratable();

			if (!reference.isActive(index)) {
				registratable.setActive(false);
			}
		}

		for (int e = 0; e < getReferenceCount(); e++) {
			CombinationReference reference = (CombinationReference) getReference(e);

			Activateable registratable = reference.getRegistratable();

			if (reference.isActive(index)) {
				registratable.setActive(true);
			}
		}

		notifyObservers();
	}

	protected int getIndex() {
		for (Memory memory : getReferrer(Memory.class)) {
			return memory.getValue();
		}
		return 0;
	}

	public void capture() {

		int index = getIndex();

		for (int e = 0; e < getReferenceCount(); e++) {
			CombinationReference reference = (CombinationReference) getReference(e);

			Activateable registratable = (Activateable) reference.getElement();

			reference.setActive(index, registratable.isActive());

			fireReferenceChanged(reference, false);
		}

		notifyObservers();
	}

	public void clear(int index) {
		for (int e = 0; e < getReferenceCount(); e++) {
			CombinationReference reference = (CombinationReference) getReference(e);

			reference.setActive(index, false);

			fireReferenceChanged(reference, false);
		}
	}

	public void swap(int index1, int index2) {
		for (int e = 0; e < getReferenceCount(); e++) {
			CombinationReference reference = (CombinationReference) getReference(e);

			boolean value1 = reference.isActive(index1);
			boolean value2 = reference.isActive(index2);

			reference.setActive(index1, value2);
			reference.setActive(index2, value1);

			fireReferenceChanged(reference, false);
		}
	}

	public void copy(int index1, int index2) {
		for (int e = 0; e < getReferenceCount(); e++) {
			CombinationReference reference = (CombinationReference) getReference(e);

			boolean value = reference.isActive(index1);

			reference.setActive(index2, value);

			fireReferenceChanged(reference, false);
		}
	}

	/**
	 * A reference of a combination to another element.
	 */
	public static class CombinationReference extends Reference {

		private boolean[] activated = new boolean[128];

		public CombinationReference(Activateable activateable) {
			super(activateable);
			
			Arrays.fill(activated, true);
		}

		public void setActive(int index, boolean active) {
			if (index < 0 || index > 127) {
				throw new IllegalArgumentException("index");
			}
			activated[index] = active;
		}

		public boolean isActive(int index) {
			if (index < 0 || index > 127) {
				throw new IllegalArgumentException("index");
			}
			return activated[index];
		}

		public Activateable getRegistratable() {
			return (Activateable) getElement();
		}
	}

	protected void notifyObservers() {
		for (Observer observer : getReferrer(Observer.class)) {
			observer.initiated(this);
		}
	}

	public static interface Observer {
		public void initiated(Combination combination);
	}
}