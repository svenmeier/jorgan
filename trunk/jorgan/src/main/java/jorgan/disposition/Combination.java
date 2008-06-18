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

public class Combination extends Switch {

	public Combination() {
		setLocking(false);
	}

	@Override
	protected boolean canReference(Class<? extends Element> clazz) {
		return Switch.class.isAssignableFrom(clazz);
	}

	@Override
	protected boolean validReference(
			jorgan.disposition.Reference<? extends Element> reference) {
		return reference.getClass() == Reference.class;
	}

	@Override
	protected Reference createReference(Element element) {
		if (!(element instanceof Switch)) {
			throw new IllegalArgumentException("can only reference Switch");
		}
		return new Reference((Switch) element);
	}

	@Override
	protected void onActivated(boolean active) {
		if (active) {
			captureOrRecall();
		}
	}

	@Override
	protected void onEngaged(boolean engaged) {
		// no need to captureOrRecall if onActivated() already did it
		if (engaged && !isActive()) {
			captureOrRecall();
		}
	}

	private void captureOrRecall() {
		for (Captor captor : getOrgan().getReferrer(this, Captor.class)) {
			if (captor.isEngaged()) {
				capture(captor);

				return;
			}
		}

		recall();
	}

	private void recall() {

		int level = getLevel();

		// deactivate first ..
		for (Reference reference : getReferences(Reference.class)) {
			Switch registratable = reference.getElement();

			if (!reference.isActive(level)) {
				registratable.setActive(false);
			}
		}

		// .. then activate
		for (Reference reference : getReferences(Reference.class)) {
			Switch registratable = reference.getElement();

			if (reference.isActive(level)) {
				registratable.setActive(true);
			}
		}
	}

	private void capture(Captor captor) {

		int level = getLevel();

		for (Reference reference : getReferences(Reference.class)) {
			reference.setActive(level, reference.getElement().isActive());

			fireChanged(reference, false);
		}

		captor.combinationCaptured();
	}

	private int getLevel() {
		int size = 1;
		int level = 0;
		if (getOrgan() != null) {
			for (Memory memory : getOrgan().getReferrer(this, Memory.class)) {
				size = memory.getSize();
				level = memory.getIndex();
				break;
			}
		}
		ensureSize(size);
		return level;
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

		private boolean[] activated = new boolean[1];

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

	// TODO call when memory changes
	private void ensureSize(int size) {
		for (Reference reference : getReferences(Reference.class)) {
			reference.setSize(size);
		}
	}
}