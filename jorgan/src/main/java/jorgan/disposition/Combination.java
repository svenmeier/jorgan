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
	protected boolean canReference(Class<? extends Element> clazz) {
		return Switch.class.isAssignableFrom(clazz);
	}

	@Override
	protected Reference createReference(Element element) {
		if (!(element instanceof Switch)) {
			throw new IllegalArgumentException("can only reference Switch");
		}
		return new Reference((Switch) element);
	}

	@Override
	public void initiate() {
		if (getOrgan() != null) {
			for (Captor captor : getOrgan().getReferrer(this, Captor.class)) {
				if (captor.isEngaged()) {
					capture();
					return;
				}
			}
		}

		recall();
	}

	public void recall() {

		int level = getLevel();

		for (Reference reference : getReferences(Reference.class)) {
			Switch registratable = reference.getElement();

			if (!reference.isActive(level)) {
				registratable.setActive(false);
			}
		}

		for (Reference reference : getReferences(Reference.class)) {
			Switch registratable = reference.getElement();

			if (reference.isActive(level)) {
				registratable.setActive(true);
			}
		}

		notifyObservers();
	}

	private int getLevel() {
		if (getOrgan() != null) {
			for (Memory memory : getOrgan().getReferrer(this, Memory.class)) {
				return memory.getIndex();
			}
		}
		return 0;
	}

	public void capture() {

		int level = getLevel();

		for (Reference reference : getReferences(Reference.class)) {
			reference.setActive(level, reference.getElement().isActive());

			fireChanged(reference, false);
		}

		notifyObservers();
	}

	public void clear(int level) {
		for (Reference reference : getReferences(Reference.class)) {
			reference.setActive(level, false);

			fireChanged(reference, false);
		}
	}

	public void swap(int level1, int level2) {
		for (Reference reference : getReferences(Reference.class)) {
			boolean value1 = reference.isActive(level1);
			boolean value2 = reference.isActive(level2);

			reference.setActive(level1, value2);
			reference.setActive(level2, value1);

			fireChanged(reference, false);
		}
	}

	public void copy(int level1, int level2) {
		for (Reference reference : getReferences(Reference.class)) {
			boolean value = reference.isActive(level1);

			reference.setActive(level2, value);

			fireChanged(reference, false);
		}
	}

	/**
	 * A reference of a combination to another element.
	 */
	public static class Reference extends jorgan.disposition.Reference<Switch> {

		private boolean[] activated = new boolean[100];

		public Reference(Switch element) {
			super(element);

			Arrays.fill(activated, true);
		}

		public void setActive(int index, boolean active) {
			if (index < 0 || index > activated.length) {
				throw new IllegalArgumentException("index");
			}
			activated[index] = active;
		}

		public boolean isActive(int index) {
			if (index < 0 || index > activated.length) {
				throw new IllegalArgumentException("index");
			}
			return activated[index];
		}

		public void setSize(int size) {
			if (activated.length != size) {
				boolean[] booleans = new boolean[size];
				System.arraycopy(activated, 0, booleans, 0, Math.min(
						activated.length, booleans.length));
				activated = booleans;
			}
		}

		@Override
		public Reference clone() {
			Reference clone = (Reference) super.clone();

			clone.activated = this.activated.clone();

			return clone;
		}
	}

	protected void notifyObservers() {
		if (getOrgan() != null) {
			for (Observer observer : getOrgan().getReferrer(this,
					Observer.class)) {
				observer.initiated(this);
			}
		}
	}

	public static interface Observer {
		public void initiated(Combination combination);
	}

	/**
	 * If a referring {@link Memory} changes, the size might change too.
	 * 
	 * @see Reference#setSize()
	 */
	@Override
	public void referrerChanged(Element element) {
		if (element instanceof Memory) {
			int size = ((Memory) element).getSize();

			for (Reference reference : getReferences(Reference.class)) {
				reference.setSize(size);
			}
		}
	}
}