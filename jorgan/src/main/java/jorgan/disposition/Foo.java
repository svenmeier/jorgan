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

import java.util.List;

/**
 * An trigger of {@link jorgan.disposition.Switch}es.
 */
public class Foo extends Element implements Observer {

	private boolean onActivated = false;

	private boolean onDeactivated = false;

	private boolean onChanged = false;

	public void setOnActivated(boolean onActivated) {
		if (this.onActivated != onActivated) {
			boolean oldOnActivated = this.onActivated;

			this.onActivated = onActivated;

			fireChange(new PropertyChange(oldOnActivated, this.onActivated));
		}
	}

	public void setOnDeactivated(boolean onDeactivated) {
		if (this.onDeactivated != onDeactivated) {
			boolean oldOnDeactivated = this.onDeactivated;

			this.onDeactivated = onDeactivated;

			fireChange(new PropertyChange(oldOnDeactivated, this.onDeactivated));
		}
	}

	public void setOnChanged(boolean onChanged) {
		if (this.onChanged != onChanged) {
			boolean oldOnChanged = this.onChanged;

			this.onChanged = onChanged;

			fireChange(new PropertyChange(oldOnChanged, this.onChanged));
		}
	}

	public boolean getOnActivated() {
		return this.onActivated;
	}

	public boolean getOnDeactivated() {
		return this.onDeactivated;
	}

	public boolean getOnChanged() {
		return this.onChanged;
	}

	@Override
	protected boolean canReference(Class<? extends Element> clazz) {
		return Switch.class.isAssignableFrom(clazz)
				|| Continuous.class.isAssignableFrom(clazz);
	}

	@Override
	public void changed(Element element) {
		if (!references((Element) element)) {
			throw new IllegalArgumentException("does not reference '" + element
					+ "'");
		}

		List<Switch> switches = getReferenced(Switch.class);
		if (!switches.isEmpty()) {
			Switch first = switches.get(0);
			if (first != element) {
				if (triggers(element)) {
					first.activate(true);
				}
			}
		}
	}

	private boolean triggers(Element element) {
		if (element instanceof Switch) {
			Switch aSwitch = (Switch) element;

			return onActivated && aSwitch.isActive() || onDeactivated
					&& !aSwitch.isActive();
		} else if (element instanceof Continuous) {
			return onChanged;
		} else {
			throw new IllegalStateException("unkown element "
					+ element.getClass());
		}

	}
}