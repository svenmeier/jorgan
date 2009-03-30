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

public class Combination extends Switch implements Activating {

	private transient boolean recalling;
	
	public Combination() {
		setLocking(false);
	}

	@Override
	protected boolean canReference(Class<? extends Element> clazz) {
		return Switch.class.isAssignableFrom(clazz) || Continuous.class.isAssignableFrom(clazz);
	}

	@Override
	protected boolean validReference(
			Reference<? extends Element> reference) {
		return AbstractReference.class.isAssignableFrom(reference.getClass());
	}

	@Override
	protected Reference<?> createReference(Element element) {
		if (element instanceof Switch) {
			return new SwitchReference((Switch) element);
		} else if (element instanceof Continuous) {
			return new ContinuousReference((Continuous) element);
			
		}
		throw new IllegalArgumentException("can only reference Switch or Continuous");
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

		try {
			recalling = true;

			int level = getLevel();

			for (AbstractReference<?> reference : getReferences(AbstractReference.class)) {
				reference.recall(level);
			}
		} finally {
			recalling = false;
		}
	}

	private void capture(Captor captor) {

		int level = getLevel();

		for (AbstractReference<?> reference : getReferences(AbstractReference.class)) {
			reference.capture(level);

			fireChange(new ReferenceChange(reference));
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
		for (AbstractReference<?> reference : getReferences(AbstractReference.class)) {
			reference.clear(level);

			fireChange(new ReferenceChange(reference));
		}
	}

	public void swap(int level1, int level2) {
		for (AbstractReference<?> reference : getReferences(AbstractReference.class)) {
			reference.swap(level1, level2);

			fireChange(new ReferenceChange(reference));
		}
	}

	public void copy(int level1, int level2) {
		for (AbstractReference<?> reference : getReferences(AbstractReference.class)) {
			reference.copy(level1, level2);

			fireChange(new ReferenceChange(reference));
		}
	}

	public void activeChanged(Switch element, boolean active) {
		if (recalling) {
			return;
		}
		
		if (!references((Element) element)) {
			throw new IllegalArgumentException("does not reference '" + element
					+ "'");
		}

		if (isActive()) {
			int level = getLevel();

			for (SwitchReference reference : getReferences(SwitchReference.class)) {
				if (!reference.matches(level)) {
					setActive(false);
					break;
				}
			}
		}
	}
	
	private static abstract class AbstractReference<E extends Element> extends Reference<E> {
		
		public AbstractReference(E element) {
			super(element);
		}
		
		public abstract void setSize(int size);
		
		public abstract void copy(int index1, int index2);

		public abstract void swap(int level1, int level2);
		
		public abstract void clear(int level);

		public abstract void capture(int index);
		
		public abstract void recall(int index);
	}
	
	/**
	 * A reference of a combination to a {@link Switch}.
	 */
	public static class SwitchReference extends AbstractReference<Switch> {

		private boolean[] actives = new boolean[1];

		public SwitchReference(Switch element) {
			super(element);

			Arrays.fill(actives, true);
		}

		public boolean matches(int index) {
			if (index < 0 || index > actives.length) {
				throw new IllegalArgumentException("index");
			}

			return actives[index] == getElement().isActive();
		}

		@Override
		public void copy(int index1, int index2) {
			if (index1 < 0 || index1 > actives.length) {
				throw new IllegalArgumentException("index1");
			}
			if (index2 < 0 || index2 > actives.length) {
				throw new IllegalArgumentException("index2");
			}
			
			actives[index2] = actives[index1];
		}

		@Override
		public void swap(int index1, int index2) {
			if (index1 < 0 || index1 > actives.length) {
				throw new IllegalArgumentException("index1");
			}
			if (index2 < 0 || index2 > actives.length) {
				throw new IllegalArgumentException("index2");
			}
			
			boolean temp = actives[index1];
			actives[index1] = actives[index2];
			actives[index2] = temp;
		}
		
		@Override
		public void clear(int index) {
			if (index < 0 || index > actives.length) {
				throw new IllegalArgumentException("index");
			}
			actives[index] = false;
		}

		@Override
		public void capture(int index) {
			if (index < 0 || index > actives.length) {
				throw new IllegalArgumentException("index");
			}
			actives[index] = getElement().isActive();
		}
		
		@Override
		public void recall(int index) {
			if (index < 0 || index > actives.length) {
				throw new IllegalArgumentException("index");
			}
			getElement().setActive(actives[index]);
		}
		
		public void setSize(int size) {
			if (actives.length != size) {
				boolean[] activated = new boolean[size];
				System.arraycopy(this.actives, 0, activated, 0, Math.min(
						this.actives.length, activated.length));
				this.actives = activated;
			}
		}

		@Override
		public SwitchReference clone() {
			SwitchReference clone = (SwitchReference) super.clone();

			clone.actives = this.actives.clone();

			return clone;
		}
	}

	/**
	 * A reference of a combination to a {@link Continuous}.
	 */
	public static class ContinuousReference extends AbstractReference<Continuous> {

		private float[] values = new float[1];

		public ContinuousReference(Continuous element) {
			super(element);

			Arrays.fill(values, 1.0f);
		}

		@Override
		public void copy(int index1, int index2) {
			if (index1 < 0 || index1 > values.length) {
				throw new IllegalArgumentException("index1");
			}
			if (index2 < 0 || index2 > values.length) {
				throw new IllegalArgumentException("index2");
			}
			
			values[index2] = values[index1];
		}

		@Override
		public void swap(int index1, int index2) {
			if (index1 < 0 || index1 > values.length) {
				throw new IllegalArgumentException("index1");
			}
			if (index2 < 0 || index2 > values.length) {
				throw new IllegalArgumentException("index2");
			}
			
			float temp = values[index1];
			values[index1] = values[index2];
			values[index2] = temp;
		}

		@Override
		public void clear(int index) {
			if (index < 0 || index > values.length) {
				throw new IllegalArgumentException("index");
			}
			values[index] = 0.0f;
		}
		
		@Override
		public void capture(int index) {
			if (index < 0 || index > values.length) {
				throw new IllegalArgumentException("index");
			}
			values[index] = getElement().getValue();
		}
		
		@Override
		public void recall(int index) {
			if (index < 0 || index > values.length) {
				throw new IllegalArgumentException("index");
			}
			getElement().setValue(values[index]);
		}
		
		public void setSize(int size) {
			if (values.length != size) {
				float[] values = new float[size];
				System.arraycopy(this.values, 0, values, 0, Math.min(
						this.values.length, values.length));
				this.values = values;
			}
		}

		@Override
		public ContinuousReference clone() {
			ContinuousReference clone = (ContinuousReference) super.clone();

			clone.values = this.values.clone();

			return clone;
		}
	}
	
	// TODO call when memory changes
	private void ensureSize(int size) {
		for (AbstractReference<?> reference : getReferences(AbstractReference.class)) {
			reference.setSize(size);
		}
	}
}