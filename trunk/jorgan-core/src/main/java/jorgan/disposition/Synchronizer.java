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
 * A synchronizer of {@link jorgan.disposition.Switch}es.
 */
public class Synchronizer extends Switch implements Observer {

	private Action whenActivated = Action.ACTIVATE;

	private Action whenDeactivated = Action.DEACTIVATE;

	public void setWhenActivated(Action whenActivated) {
		if (this.whenActivated != whenActivated) {
			Action oldWhenActivated = this.whenActivated;

			this.whenActivated = whenActivated;

			fireChange(new PropertyChange(oldWhenActivated, this.whenActivated));
		}
	}

	public Action getWhenActivated() {
		return this.whenActivated;
	}

	public void setWhenDeactivated(Action whenDeactivated) {
		if (this.whenDeactivated != whenDeactivated) {
			Action oldWhenDeactivated = this.whenDeactivated;

			this.whenDeactivated = whenDeactivated;

			fireChange(new PropertyChange(oldWhenDeactivated,
					this.whenDeactivated));
		}
	}

	public Action getWhenDeactivated() {
		return this.whenDeactivated;
	}

	@Override
	protected boolean canReference(Class<? extends Element> clazz) {
		return Switch.class.isAssignableFrom(clazz);
	}

	@Override
	public void changed(Element element) {
		if (!references(element)) {
			throw new IllegalArgumentException("does not reference '" + element
					+ "'");
		}

		if (isEngaged()) {
			Switch changed = (Switch) element;
			Switch first = (Switch) getReference(0).getElement();
			if (changed != first) {
				if (changed.isActive()) {
					whenActivated.perform(first);
				} else if (!changed.isActive()) {
					whenDeactivated.perform(first);
				}
			}
		}
	}

	public static enum Action {
		IGNORE {
			@Override
			public void perform(Switch element) {
			}
		},
		ACTIVATE {
			@Override
			public void perform(Switch element) {
				element.setActive(true);
			}
		},
		DEACTIVATE {
			@Override
			public void perform(Switch element) {
				element.setActive(false);
			}
		};

		public abstract void perform(Switch element);
	}
}