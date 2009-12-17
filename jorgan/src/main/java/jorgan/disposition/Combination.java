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

public class Combination extends Switch implements Continuous.Dependent,
		Switch.Dependent {

	private transient boolean recalling;

	public Combination() {
		setLocking(false);
	}

	@Override
	protected boolean canReference(Class<? extends Element> clazz) {
		return Switch.class.isAssignableFrom(clazz)
				|| Continuous.class.isAssignableFrom(clazz);
	}

	@Override
	protected boolean validReference(Reference<? extends Element> reference) {
		return AbstractReference.class.isAssignableFrom(reference.getClass());
	}

	@Override
	protected Reference<?> createReference(Element element) {
		if (element instanceof Switch) {
			return new SwitchReference((Switch) element);
		} else if (element instanceof Continuous) {
			return new ContinuousReference((Continuous) element);

		}
		throw new IllegalArgumentException(
				"can only reference Switch or Continuous");
	}

	@Override
	protected void onEngaged(boolean engaged) {
		if (engaged) {
			recall();

			for (Captor captor : getOrgan().getReferrer(this, Captor.class)) {
				captor.setActive(false);
			}
		}
	}

	/**
	 * Capture on explicit activation only. 
	 */
	@Override
	protected void onActivated(boolean active) {
		if (active) {
			for (Captor captor : getOrgan().getReferrer(this, Captor.class)) {
				if (captor.isActive()) {
					capture(captor);

					return;
				}
			}

			recall();
		}
	}
	
	private void recall() {

		try {
			recalling = true;

			for (AbstractReference<?> reference : getReferences(AbstractReference.class)) {
				reference.recall();
			}
		} finally {
			recalling = false;
		}
	}

	private void capture(Captor captor) {

		for (AbstractReference<?> reference : getReferences(AbstractReference.class)) {
			reference.capture();

			fireChange(new FastReferenceChange(reference));
		}

		captor.setActive(false);
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
			for (SwitchReference reference : getReferences(SwitchReference.class)) {
				if (!reference.matches()) {
					setActive(false);
					break;
				}
			}
		}
	}

	public void valueChanged(Continuous element, float value) {
		if (recalling) {
			return;
		}

		if (!references((Element) element)) {
			throw new IllegalArgumentException("does not reference '" + element
					+ "'");
		}

		if (isActive()) {
			for (ContinuousReference reference : getReferences(ContinuousReference.class)) {
				if (!reference.matches()) {
					setActive(false);
					break;
				}
			}
		}
	}

	public boolean isRecalling() {
		return recalling;
	}

	private static abstract class AbstractReference<E extends Element> extends
			Reference<E> {

		public AbstractReference(E element) {
			super(element);
		}

		public abstract void capture();

		public abstract void recall();
	}

	/**
	 * A reference of a combination to a {@link Switch}.
	 */
	public static class SwitchReference extends AbstractReference<Switch> {

		private boolean active = true;

		public SwitchReference(Switch element) {
			super(element);
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public boolean isActive() {
			return active;
		}

		public boolean matches() {
			return active == getElement().isActive();
		}

		@Override
		public void capture() {
			active = getElement().isActive();
		}

		@Override
		public void recall() {
			getElement().setActive(active);
		}

		@Override
		public SwitchReference clone() {
			SwitchReference clone = (SwitchReference) super.clone();

			clone.active = this.active;

			return clone;
		}
	}

	/**
	 * A reference of a combination to a {@link Continuous}.
	 */
	public static class ContinuousReference extends
			AbstractReference<Continuous> {

		private float value = 1.0f;

		public ContinuousReference(Continuous element) {
			super(element);
		}

		public boolean matches() {
			return value == getElement().getValue();
		}

		public float getValue() {
			return value;
		}

		public void setValue(float value) {
			this.value = value;
		}

		@Override
		public void capture() {
			value = getElement().getValue();
		}

		@Override
		public void recall() {
			getElement().setValue(value);
		}

		@Override
		public ContinuousReference clone() {
			ContinuousReference clone = (ContinuousReference) super.clone();

			clone.value = this.value;

			return clone;
		}
	}
}